package ru.baklykov.app.web.model.response.question

import ru.baklykov.app.web.model.response.answer.AnswerResponse
import ru.baklykov.app.web.model.response.theme.ThemeResponse
import java.time.Instant
import java.util.*

data class GetQuestionInfoResponse(
    val questionId: UUID,
    val ownerId: UUID,
    val title: String,
    val themes: List<ThemeResponse>,
    val pictures: List<UUID>,
    val description: String,
    val answers: List<AnswerResponse>,
    val createdAt: Instant
)
