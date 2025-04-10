package ru.baklykov.app.web.model.response.room

import java.util.*

data class GetRoomInfoResponse(
    val roomId: UUID,
    val name: String,
    val teacherId: UUID,
    val students: List<UUID>,
    val isClosed: Boolean,
    val games: List<UUID>,
    val dateOfCreating: String
)
