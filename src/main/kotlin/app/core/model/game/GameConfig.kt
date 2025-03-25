package ru.baklykov.app.core.model.game

import java.time.ZonedDateTime
import java.util.*

data class GameConfig(
    val categories: List<UUID> ?= listOf(),
    val questionCount: Int ?= 30,
    val duration: Int ?= 30,
    val gameType: GameType ?= GameType.SINGLE,
    val difficulty: DifficultyLevel ?= DifficultyLevel.MEDIUM,
    val allowSkips: Boolean = false,
    val enableHints: Boolean = false,
    val questions: List<UUID> ?= listOf(),
    val startDate: ZonedDateTime? = ZonedDateTime.now()
)
