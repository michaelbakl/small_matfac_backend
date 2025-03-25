package app.core.service.teacher

import app.core.converter.TeacherConverter
import app.core.exception.NotFoundException
import app.core.exception.RepositoryException
import app.core.exception.ServiceException
import app.core.repository.teacher.ITeacherRepository
import app.core.service.login.LoginService
import app.web.model.Login
import app.web.model.response.person.GetTeacherInfoResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import ru.baklykov.app.core.model.person.Teacher
import java.time.LocalDateTime
import java.util.*

open class TeacherService(private val repository: ITeacherRepository, private val loginService: LoginService) : ITeacherService {
    private val LOGGER: Logger = LoggerFactory.getLogger(this.javaClass)

    @Transactional
    override fun createActualTeacher(login: Login, teacher: Teacher): GetTeacherInfoResponse {
        LOGGER.debug("SERVICE add actual teacher {}", teacher)
        try {
            val roles = listOf("USER", "TEACHER")
            val userId: UUID = loginService.create(login, roles)
            val updatedTeacher = teacher.copy(userId = userId)
            repository.addActualTeacher(updatedTeacher)
            return getActualTeacher(teacher.personId)
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE error adding actual teacher {}", teacher)
            throw ServiceException("SERVICE add actual teacher exception", e)
        }
    }

    override fun getActualTeacher(id: UUID): GetTeacherInfoResponse {
        LOGGER.debug("SERVICE get actual teacher by id {}", id)
        try {
            val teacher: Teacher = repository.getActualTeacher(id) ?: throw NotFoundException("Teacher not found")
            return TeacherConverter.convertToResponse(teacher)
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE error getting actual teacher by id {}", id)
            throw ServiceException("SERVICE get actual teacher exception", e)
        }
    }

    @Transactional
    override fun updateActualTeacher(teacher: Teacher, dateOfChanging: LocalDateTime): GetTeacherInfoResponse {
        LOGGER.debug("SERVICE update actual teacher {}", teacher)
        try {
            repository.addHistoryTeacher(teacher, dateOfChanging)
            repository.updateActualTeacher(teacher)
            return getActualTeacher(teacher.personId)
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE error updating actual teacher {}", teacher)
            throw ServiceException("SERVICE update actual teacher", e)
        }
    }

    @Transactional
    override fun deleteActualTeacher(id: UUID): Int {
        LOGGER.debug("SERVICE delete actual teacher by id {}", id)
        try {
            return repository.deleteActualTeacher(id)
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE error deleting actual teacher by id {}", id)
            throw ServiceException("SERVICE delete actual teacher", e)
        }
    }

    @Deprecated("Use only update actual teacher")
    @Transactional
    override fun addHistoryTeacher(teacher: Teacher, dateOfChanging: LocalDateTime): GetTeacherInfoResponse {
        LOGGER.debug("SERVICE add history teacher {}, {}", teacher, dateOfChanging)
        try {
            repository.addHistoryTeacher(teacher, dateOfChanging)
            return getHistoryTeacher(teacher.personId, dateOfChanging)
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE error adding history teacher {}, {}", teacher, dateOfChanging)
            throw ServiceException("SERVICE add history teacher exception", e)
        }
    }

    override fun getHistoryTeacher(id: UUID, dateOfChanging: LocalDateTime): GetTeacherInfoResponse {
        LOGGER.debug("SERVICE get history teacher by id {}, {}", id, dateOfChanging)
        try {
            val teacher: Teacher = repository.getHistoryTeacher(id, dateOfChanging) ?: throw NotFoundException("History teacher not found")
            return TeacherConverter.convertToResponse(teacher)
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE error getting history teacher by id {}, {}", id, dateOfChanging)
            throw ServiceException("SERVICE get history teacher exception", e)
        }
    }

    @Transactional
    override fun updateHistoryTeacher(teacher: Teacher, dateOfChanging: LocalDateTime, historyId: UUID): GetTeacherInfoResponse {
        LOGGER.debug("SERVICE update history teacher {}, {}", teacher, dateOfChanging)
        try {
            repository.updateHistoryTeacher(teacher, dateOfChanging)
            return getHistoryTeacher(teacher.personId, dateOfChanging)
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE error updating history teacher {}, {}", teacher, dateOfChanging)
            throw ServiceException("SERVICE update history teacher exception", e)
        }
    }

    @Transactional
    override fun deleteHistoryTeacher(id: UUID, dateOfChanging: LocalDateTime): Int {
        LOGGER.debug("SERVICE delete history teacher {}, {}", id, dateOfChanging)
        try {
            return repository.deleteHistoryTeacher(id, dateOfChanging)
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE error deleting history teacher {}, {}", id, dateOfChanging)
            throw ServiceException("SERVICE delete history teacher exception", e)
        }
    }

    override fun getAllActualTeachers(): List<GetTeacherInfoResponse> {
        LOGGER.debug("SERVICE get all actual teachers",)
        try {
            val list: List<Teacher> = repository.getAllActualTeachers()
            return TeacherConverter.convertToResponseList(list)
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE error getting all actual teachers")
            throw ServiceException("SERVICE get all actual teachers exception", e)
        }
    }

    override fun getTeacherInfoByUserId(userId: UUID): GetTeacherInfoResponse {
        LOGGER.debug("SERVICE get actual teacher by user id {}", userId)
        try {
            val teacherId = repository.getTeacherId(userId);
            teacherId?.let {
                val teacher: Teacher = repository.getActualTeacher(it) ?: throw NotFoundException("Teacher not found by user id")
                return TeacherConverter.convertToResponse(teacher)
            } ?: throw NotFoundException("Teacher not found by user id")
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE error getting actual teacher by user id {}", userId)
            throw ServiceException("SERVICE get actual teacher by user id exception", e)
        }
    }

}
