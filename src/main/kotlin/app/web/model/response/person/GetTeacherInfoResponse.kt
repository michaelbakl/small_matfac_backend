package app.web.model.response.person

import java.time.LocalDateTime
import java.util.*

data class GetTeacherInfoResponse (
    val teacherId: UUID,
    val userId: UUID,
    val surname: String,
    val name: String,
    val middleName: String?,
    val email: String,
    val dateOfBirth: LocalDateTime? = null,
    var dateOfEntering: LocalDateTime? = null
)
