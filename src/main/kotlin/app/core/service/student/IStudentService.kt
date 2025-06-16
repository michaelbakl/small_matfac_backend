package app.core.service.student

import app.core.filter.StudentFilter
import app.web.model.Login
import app.web.model.response.person.GetStudentInfoResponse
import app.web.model.response.student.GetStudentsResponse
import app.core.model.person.Student
import java.time.ZonedDateTime
import java.util.*

interface IStudentService {

    fun getStudentByGroups(groups: List<UUID>): GetStudentsResponse

    fun addActualStudent(login: Login, student: Student): GetStudentInfoResponse

    fun getActualStudent(id: UUID): GetStudentInfoResponse

    fun updateActualStudent(student: Student, dateOfChanging: ZonedDateTime): GetStudentInfoResponse

    fun deleteActualStudent(id: UUID): Int

    @Deprecated("Use only update student")
    fun addHistoryStudent(student: Student, dateOfChanging: ZonedDateTime): GetStudentInfoResponse

    fun getHistoryStudent(id: UUID, dateOfChanging: ZonedDateTime): GetStudentInfoResponse

    fun updateHistoryStudent(student: Student, dateOfChanging: ZonedDateTime, historyId: UUID): GetStudentInfoResponse

    fun deleteHistoryStudent(id: UUID, dateOfChanging: ZonedDateTime): Int

    /**
     * finds students that match the condition in filter
     * @param filter - filter with conditions
     * @return list of matching objects
     */
    fun getStudentsByFilter(filter: StudentFilter): List<GetStudentInfoResponse>

}