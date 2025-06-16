package app.core.filter

import java.time.ZonedDateTime
import java.util.*

data class StudentFilter (

    val id: UUID? = null,
    val studentId: UUID? = null,
    val surname: String? = null,
    val name: String? = null,
    val middleName: String? = null,
    val email: String? = null,
    val dateOfBirthL: ZonedDateTime? = null,
    val dateOfBirthR: ZonedDateTime? = null,
    val dateOfEnteringL: ZonedDateTime? = null,
    val dateOfEnteringR: ZonedDateTime? = null,
    val groups: List<UUID>? = null,
    val rooms: List<UUID>? = null,
    val games: List<UUID>? = null,

)
