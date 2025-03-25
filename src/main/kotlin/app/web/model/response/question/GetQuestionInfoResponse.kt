package ru.baklykov.app.web.model.response.question

import java.util.*

data class GetQuestionInfoResponse(
    val questionId: UUID,
    val ownerId: UUID,
    val title: String,
    val themes: List<UUID>,
    val pictures: List<UUID>,
    val description: String,
    val answers: Map<UUID, Pair<Boolean, Int>>
)
