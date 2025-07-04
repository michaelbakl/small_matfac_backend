package app.core.model.person

import java.time.ZonedDateTime
import java.util.*

data class Teacher(
    val personId: UUID,
    val userId: UUID,
    val surname: String,
    val name: String,
    val middleName: String?,
    val email: String,
    val dateOfBirth: ZonedDateTime? = null,
    val dateOfEntering: ZonedDateTime? = null
)
