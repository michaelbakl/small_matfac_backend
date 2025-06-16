package app.core.model

import java.time.ZonedDateTime
import java.util.*

data class Room (
    val roomId: UUID,
    val name: String,
    val teacherId: UUID,
    val students: List<UUID>,
    val isClosed: Boolean,
    val games: List<UUID> ?= listOf(),
    val dateOfCreating: ZonedDateTime ?= null
)
