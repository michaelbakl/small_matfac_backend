package app.core.service.user

import app.core.exception.NotFoundException
import app.core.exception.RepositoryException
import app.core.exception.ServiceException
import app.core.repository.student.IStudentRepository
import app.core.repository.teacher.ITeacherRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class CredentialsService(
    private val studentRepository: IStudentRepository,
    private val teacherRepository: ITeacherRepository
): ICredentialsService {

    private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)

    override fun findTeacherIdByUserId(userId: UUID): UUID {
        try {
            LOGGER.debug("SERVICE get teacher id by user id {}", userId)
            return teacherRepository.getTeacherId(userId)?: throw NotFoundException("Teacher id not found")
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error getting teacher id by user id {}", userId)
            throw ServiceException("SERVICE get teacher id by user id exception", e)
        }
    }

    override fun findStudentIdByUserId(userId: UUID): UUID {
        try {
            LOGGER.debug("SERVICE get student id by user id {}", userId)
            return studentRepository.getStudentId(userId)?: throw NotFoundException("Student id not found")
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error getting student id by user id {}", userId)
            throw ServiceException("SERVICE get student id by user id exception", e)
        }
    }

//    override fun findTeacherIdByLessonId(lessonId: UUID): UUID {
//        try {
//            LOGGER.debug("SERVICE get teacher id by lesson id {}", lessonId)
//            return lessonRepository.getTeacherIdByLessonId(lessonId) ?: throw NotFoundException("Teacher id not found")
//        } catch (e: RepositoryException) {
//            LOGGER.error("SERVICE error getting teacher id by lesson id {}", lessonId)
//            throw ServiceException("SERVICE get teacher id by lesson id exception", e)
//        }
//    }

}
