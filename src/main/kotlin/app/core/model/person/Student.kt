package ru.baklykov.app.core.model.person

import java.time.LocalDateTime
import java.util.*

data class Student(
    val personId: UUID,
    val userId: UUID,
    val surname: String,
    val name: String,
    val middleName: String?,
    val email: String,
    val dateOfBirth: LocalDateTime? = null,
    var dateOfEntering: LocalDateTime? = null,
    val groups: List<StudentGroupInfo> = listOf(),
)