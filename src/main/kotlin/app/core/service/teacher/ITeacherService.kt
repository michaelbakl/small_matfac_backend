package app.core.service.teacher

import app.web.model.Login
import app.web.model.response.person.GetTeacherInfoResponse
import ru.baklykov.app.core.model.person.Teacher
import java.time.LocalDateTime
import java.util.*

interface ITeacherService {

    fun createActualTeacher(login: Login, teacher: Teacher): GetTeacherInfoResponse

    fun getActualTeacher(id: UUID): GetTeacherInfoResponse

    fun updateActualTeacher(teacher: Teacher, dateOfChanging: LocalDateTime): GetTeacherInfoResponse

    fun deleteActualTeacher(id: UUID): Int

    @Deprecated("Use only update actual teacher")
    fun addHistoryTeacher(teacher: Teacher, dateOfChanging: LocalDateTime): GetTeacherInfoResponse

    fun getHistoryTeacher(id: UUID, dateOfChanging: LocalDateTime): GetTeacherInfoResponse

    fun updateHistoryTeacher(teacher: Teacher, dateOfChanging: LocalDateTime, historyId: UUID): GetTeacherInfoResponse

    fun deleteHistoryTeacher(id: UUID, dateOfChanging: LocalDateTime): Int

    fun getAllActualTeachers(): List<GetTeacherInfoResponse>

    fun getTeacherInfoByUserId(userId: UUID): GetTeacherInfoResponse

}
