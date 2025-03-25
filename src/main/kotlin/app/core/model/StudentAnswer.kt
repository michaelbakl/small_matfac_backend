package ru.baklykov.app.core.model

import java.time.LocalDateTime
import java.util.*

data class StudentAnswer (
    val studentId: UUID,
    val questionId: UUID,
    val chosenAnswers: List<Answer>,
    val timeOfAnswering: LocalDateTime
)
