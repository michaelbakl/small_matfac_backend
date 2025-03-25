package app.core.filter

import java.time.LocalDateTime
import java.util.*

data class TeacherFilter (

    val id: UUID?,
    val teacherId: UUID?,
    val surname: String?,
    val name: String?,
    val middleName: String?,
    val email: String?,
    val dateOfBirth: LocalDateTime?,

)