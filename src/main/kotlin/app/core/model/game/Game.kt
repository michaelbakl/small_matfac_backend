package app.core.model.game

import app.core.model.question.Question
import java.util.*

data class Game(
    val gameId: UUID,
    val roomId: UUID,
    val creatorId: UUID,
    val questions: List<Question>,
    val config: GameConfig,
    val status: String
)
