package app.core.service.user

import java.util.*

interface ICredentialsService {

    fun findTeacherIdByUserId(userId: UUID): UUID

    fun findStudentIdByUserId(userId: UUID): UUID

//    fun findTeacherIdByLessonId(lessonId: UUID): UUID

}