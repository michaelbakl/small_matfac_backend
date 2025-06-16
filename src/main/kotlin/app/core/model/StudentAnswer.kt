package app.core.model

import java.time.ZonedDateTime
import java.util.*

data class StudentAnswer (
    val studentId: UUID,
    val questionId: UUID,
    val chosenAnswers: List<Answer>,
    val timeOfAnswering: ZonedDateTime
)
