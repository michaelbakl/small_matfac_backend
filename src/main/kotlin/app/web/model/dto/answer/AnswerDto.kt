package ru.baklykov.app.web.model.dto.answer

import java.util.*

data class AnswerDto(
    val id: UUID?,
    val text: String,
    val correct: Boolean,
    val points: Int
)
