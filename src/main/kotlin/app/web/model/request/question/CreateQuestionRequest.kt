package ru.baklykov.app.web.model.request.question

import ru.baklykov.app.web.model.dto.answer.AnswerDto
import java.util.*

data class CreateQuestionRequest(
    val title: String,
    val description: String,
    val pictures: List<String> = emptyList(),
    val answers: List<AnswerDto>,
    val themeIds: List<UUID>? = null,
    val type: String
)
