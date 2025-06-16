package app.core.model.game

import java.time.ZonedDateTime
import java.util.*

data class GameSession(
    val sessionId: UUID,
    val gameId: UUID,
    val studentId: UUID,
    val startedAt: ZonedDateTime,
    val finishedAt: ZonedDateTime? = null,
    val currentQuestionIndex: Int,
    val answers: List<AnswerSubmission> = emptyList()
)