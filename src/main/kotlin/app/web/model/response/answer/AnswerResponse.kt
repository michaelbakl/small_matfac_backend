package ru.baklykov.app.web.model.response.answer

import java.util.*

data class AnswerResponse(
    val id: UUID,
    val text: String,
    val correct: Boolean,
    val points: Int
)
