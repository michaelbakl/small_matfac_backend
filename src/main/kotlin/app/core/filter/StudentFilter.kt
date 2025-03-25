package app.core.filter

import java.time.LocalDateTime
import java.util.*

data class StudentFilter (

    val id: UUID? = null,
    val studentId: UUID? = null,
    val surname: String? = null,
    val name: String? = null,
    val middleName: String? = null,
    val email: String? = null,
    val dateOfBirth: LocalDateTime? = null,
    val dateOfEntering: LocalDateTime? = null,
    val group: UUID? = null,
)
