package app.core.service.teacher

import app.core.filter.TeacherFilter
import app.web.model.Login
import app.web.model.response.person.GetTeacherInfoResponse
import app.core.model.person.Teacher
import java.time.ZonedDateTime
import java.util.*

interface ITeacherService {

    fun createActualTeacher(login: Login, teacher: Teacher): GetTeacherInfoResponse

    fun getActualTeacher(id: UUID): GetTeacherInfoResponse

    fun updateActualTeacher(teacher: Teacher, dateOfChanging: ZonedDateTime): GetTeacherInfoResponse

    fun deleteActualTeacher(id: UUID): Int

    @Deprecated("Use only update actual teacher")
    fun addHistoryTeacher(teacher: Teacher, dateOfChanging: ZonedDateTime): GetTeacherInfoResponse

    fun getHistoryTeacher(id: UUID, dateOfChanging: ZonedDateTime): GetTeacherInfoResponse

    fun updateHistoryTeacher(teacher: Teacher, dateOfChanging: ZonedDateTime, historyId: UUID): GetTeacherInfoResponse

    fun deleteHistoryTeacher(id: UUID, dateOfChanging: ZonedDateTime): Int

    fun getAllActualTeachers(): List<GetTeacherInfoResponse>

    fun getTeacherInfoByUserId(userId: UUID): GetTeacherInfoResponse

    /**
     * returns list of teachers by filter
     * @param filter - filter for searching
     * @return list of teachers that match the conditions in filter
     */
    fun getTeachersByFilter(filter: TeacherFilter): List<GetTeacherInfoResponse>

}
