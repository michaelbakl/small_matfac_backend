package ru.baklykov.app.core.model

import java.util.*

data class Room (
    val roomId: UUID,
    val name: String,
    val teacherId: UUID,
    val students: List<UUID>,
    val isClosed: Boolean,
    val games: List<UUID> ?= listOf(),
    val dateOfCreating: String
)
