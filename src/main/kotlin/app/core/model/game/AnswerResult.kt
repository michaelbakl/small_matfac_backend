package app.core.model.game

import java.util.*

data class AnswerResult(
    val isCorrect: Boolean,
    val nextQuestionId: UUID?,
    val gameFinished: Boolean
)