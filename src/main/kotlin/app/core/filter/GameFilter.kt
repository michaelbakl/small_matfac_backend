package app.core.filter

import app.core.model.game.DifficultyLevel
import app.core.model.game.GameType
import java.time.ZonedDateTime
import java.util.*

data class GameFilter (
    val gameId: UUID ?= null,
    val roomId: UUID ?= null,
    val creatorId: UUID ?= null,
    val questions: List<UUID> ?= null,
    val status: String ?= null,
    //val categories: List<UUID> ?= null,
    val questionCount: Int ?= null,
    val name: String ?= null,
    val duration: Int ?= null,
    val gameType: GameType ?= null,
    val difficulty: DifficultyLevel ?= null,
    val allowSkips: Boolean ?= null,
    val enableHints: Boolean ?= null,
    val startDateL: ZonedDateTime ?= null,
    val startDateR: ZonedDateTime ?= null,
    val finishDateL: ZonedDateTime ?= null,
    val finishDateR: ZonedDateTime ?= null,
    val limit: Int? = 10,
    val offset: Int? = 0
)
