package ru.baklykov.app.core.model.person

import java.time.LocalDateTime
import java.util.*

data class Teacher(
    val personId: UUID,
    val userId: UUID,
    val surname: String,
    val name: String,
    val middleName: String?,
    val email: String,
    val dateOfBirth: LocalDateTime? = null,
    val dateOfEntering: LocalDateTime? = null
)
