package app.core.service.student

import app.core.exception.NotFoundException
import app.core.exception.RepositoryException
import app.core.exception.ServiceException
import app.core.exception.SignUpException
import app.core.filter.StudentFilter
import app.core.repository.student.IStudentRepository
import app.core.service.login.LoginService
import app.web.model.Login
import app.web.model.response.person.GetStudentInfoResponse
import app.web.model.response.student.GetStudentsResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.baklykov.app.core.converter.StudentConverter
import app.core.model.person.Student
import app.core.service.user.UserRegistrationService
import java.time.ZonedDateTime
import java.util.*


@Service
open class StudentService(private val repository: IStudentRepository, private val registrationService: UserRegistrationService) : IStudentService {

    private val LOGGER: Logger = LoggerFactory.getLogger(this.javaClass)

    //TODO(change)
    @Async
    override fun getStudentByGroups(groups: List<UUID>): GetStudentsResponse {
        LOGGER.debug("SERVICE get students by groups {}", groups)
        try {
            val finalMap: MutableMap<UUID, List<GetStudentInfoResponse>> = mutableMapOf()
            groups.map { group ->
                val filter = StudentFilter(groups = listOf(group))
                val result: List<Student> = repository.getWithParams(filter)
                finalMap[group] = StudentConverter.convertToResponseList(result)
            }
            return GetStudentsResponse(finalMap)
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE error getting students by groups {}", groups)
            throw ServiceException("SERVICE get students by groups exception", e)
        }
    }

    @Transactional(rollbackFor = [RepositoryException::class, SignUpException::class])
    override fun addActualStudent(login: Login, student: Student): GetStudentInfoResponse {
        LOGGER.debug("SERVICE add actual student {}", student)
        try {
            val userId = registrationService.create(login, listOf("USER", "STUDENT"))
            val studentId = UUID.randomUUID()
            val finalStudent = student.copy(userId = userId, personId = studentId)
            repository.addActualStudent(finalStudent)
            finalStudent.groups.map { group ->
                repository.addStudentGroup(
                    UUID.randomUUID(),
                    finalStudent.personId,
                    group.groupId,
                    group.startDate,
                    group.endDate,
                    group.actual ?: true
                )
            }
            return getActualStudent(finalStudent.personId)
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE error adding actual student {}", student)
            throw ServiceException("SERVICE add actual student exception", e)
        }
    }

    @Async
    override fun getActualStudent(id: UUID): GetStudentInfoResponse {
        LOGGER.debug("SERVICE get actual student by id {}", id)
        try {
            val student: Student = repository.getActualStudent(id) ?: throw NotFoundException("Student not found")
            return StudentConverter.convertToResponse(student)
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE error getting actual student by id {}", id)
            throw ServiceException("SERVICE get actual student exception", e)
        }
    }

    @Async
    @Transactional(rollbackFor = [RepositoryException::class])
    override fun updateActualStudent(student: Student, dateOfChanging: ZonedDateTime): GetStudentInfoResponse {
        LOGGER.debug("SERVICE update actual student {}", student)
        try {
            repository.addHistoryStudent(student, dateOfChanging)
            repository.updateActualStudent(student)
            student.groups.map { group ->
                repository.updateStudentGroup(
                    group.studentGroupId,
                    student.personId,
                    group.groupId,
                    group.startDate,
                    group.endDate,
                    if (group.actual != null) group.actual else true
                )
            }
            return getActualStudent(student.personId)
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE error updating actual student {}", student)
            throw ServiceException("SERVICE update actual student", e)
        }
    }

    @Async
    @Transactional(rollbackFor = [RepositoryException::class])
    override fun deleteActualStudent(id: UUID): Int {
        LOGGER.debug("SERVICE delete actual student by id {}", id)
        try {
            return repository.deleteActualStudent(id)
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE error deleting actual student by id {}", id)
            throw ServiceException("SERVICE delete actual student", e)
        }
    }

    @Deprecated("Use only update student")
    @Async
    @Transactional(rollbackFor = [RepositoryException::class])
    override fun addHistoryStudent(student: Student, dateOfChanging: ZonedDateTime): GetStudentInfoResponse {
        LOGGER.debug("SERVICE add history student {}, {}", student, dateOfChanging)
        try {
            repository.addHistoryStudent(student, dateOfChanging)
            return getHistoryStudent(student.personId, dateOfChanging)
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE error adding history student {}, {}", student, dateOfChanging)
            throw ServiceException("SERVICE add history student exception", e)
        }
    }

    @Async
    override fun getHistoryStudent(id: UUID, dateOfChanging: ZonedDateTime): GetStudentInfoResponse {
        LOGGER.debug("SERVICE get history student by id {}, {}", id, dateOfChanging)
        try {
            val student: Student =
                repository.getHistoryStudent(id, dateOfChanging) ?: throw NotFoundException("History student not found")
            return StudentConverter.convertToResponse(student)
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE error getting history student by id {}, {}", id, dateOfChanging)
            throw ServiceException("SERVICE get history student exception", e)
        }
    }

    @Async
    @Transactional(rollbackFor = [RepositoryException::class])
    override fun updateHistoryStudent(
        student: Student,
        dateOfChanging: ZonedDateTime,
        historyId: UUID
    ): GetStudentInfoResponse {
        LOGGER.debug("SERVICE update history student {}, {}, {}", student, dateOfChanging, historyId)
        try {
            repository.updateHistoryStudent(student, dateOfChanging)
            student.groups.map { group ->
                repository.updateHistoryStudentGroup(
                    historyId,
                    group.studentGroupId,
                    dateOfChanging
                )
            }
            return getHistoryStudent(student.personId, dateOfChanging)
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE error updating history student {}, {}, {}", student, dateOfChanging, historyId)
            throw ServiceException("SERVICE update history student exception", e)
        }
    }

    @Async
    @Transactional(rollbackFor = [RepositoryException::class])
    override fun deleteHistoryStudent(id: UUID, dateOfChanging: ZonedDateTime): Int {
        LOGGER.debug("SERVICE delete history student {}, {}", id, dateOfChanging)
        try {
            return repository.deleteHistoryStudent(id, dateOfChanging)
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE error deleting history student {}, {}", id, dateOfChanging)
            throw ServiceException("SERVICE delete history student exception", e)
        }
    }

    override fun getStudentsByFilter(filter: StudentFilter): List<GetStudentInfoResponse> {
        LOGGER.debug("SERVICE get students by filter {}", filter)
        try {
            val students: List<Student> = repository.getWithParams(filter)
            return StudentConverter.convertToResponseList(students)
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE error getting students by filter {}", filter)
            throw ServiceException("SERVICE get students by filter exception", e)
        }
    }
}
