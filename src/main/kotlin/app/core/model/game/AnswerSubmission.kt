package app.core.model.game

import java.time.ZonedDateTime
import java.util.*

data class AnswerSubmission(
    val submissionId: UUID,
    val sessionId: UUID,
    val questionId: UUID,
    val selectedOptionId: UUID?,
    val typedAnswer: String?,
    val isCorrect: Boolean,
    val submittedAt: ZonedDateTime
)
