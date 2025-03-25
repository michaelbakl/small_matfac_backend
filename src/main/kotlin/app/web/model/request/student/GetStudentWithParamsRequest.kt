package app.web.model.request.student

import java.time.LocalDateTime
import java.util.*

data class GetStudentWithParamsRequest (
    val id: String?,
    val studentId: String?,
    val surname: String?,
    val name: String?,
    val middleName: String?,
    val email: String?,
    val dateOfBirth: String?,
    val dateOfEntering: String?,
    val groups: List<String>?,
)