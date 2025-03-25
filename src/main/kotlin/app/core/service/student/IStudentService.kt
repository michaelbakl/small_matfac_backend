package app.core.service.student

import app.web.model.Login
import app.web.model.response.person.GetStudentInfoResponse
import app.web.model.response.student.GetStudentsResponse
import ru.baklykov.app.core.model.person.Student
import java.time.LocalDateTime
import java.util.*

interface IStudentService {

    fun getStudentByGroups(groups: List<UUID>): GetStudentsResponse

    fun addActualStudent(login: Login, student: Student): GetStudentInfoResponse

    fun getActualStudent(id: UUID): GetStudentInfoResponse

    fun updateActualStudent(student: Student, dateOfChanging: LocalDateTime): GetStudentInfoResponse

    fun deleteActualStudent(id: UUID): Int

    @Deprecated("Use only update student")
    fun addHistoryStudent(student: Student, dateOfChanging: LocalDateTime): GetStudentInfoResponse

    fun getHistoryStudent(id: UUID, dateOfChanging: LocalDateTime): GetStudentInfoResponse

    fun updateHistoryStudent(student: Student, dateOfChanging: LocalDateTime, historyId: UUID): GetStudentInfoResponse

    fun deleteHistoryStudent(id: UUID, dateOfChanging: LocalDateTime): Int

}