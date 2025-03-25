package ru.baklykov.app.core.model

import ru.baklykov.app.core.model.game.Game
import java.util.*

data class Room (
    val roomId: UUID,
    val name: String,
    val teacherId: UUID,
    val students: List<UUID>,
    val isClosed: Boolean,
    val games: List<Game> ?= listOf()
)
