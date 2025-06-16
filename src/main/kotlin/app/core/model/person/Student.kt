package app.core.model.person

import java.time.ZonedDateTime
import java.util.*

data class Student(
    val personId: UUID,
    val userId: UUID,
    val surname: String,
    val name: String,
    val middleName: String?,
    val email: String,
    val dateOfBirth: ZonedDateTime? = null,
    var dateOfEntering: ZonedDateTime? = null,
    val groups: List<StudentGroupInfo> = listOf(),
)