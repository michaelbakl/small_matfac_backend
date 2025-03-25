package ru.baklykov.app.core.model

import java.util.*

/**
 * @param questionId - id
 * @param title - title of the question
 * @param themes - themes of question
 * @param pictures - pictures of the question, can be empty
 * @param description - question text
 * @param answers - map answer id -> pair right or wrong and points
 */
data class Question (
    val questionId: UUID,
    val ownerId: UUID,
    val title: String,
    val themes: List<UUID>,
    val pictures: List<UUID>,
    val description: String,
    val answers: Map<UUID, Pair<Boolean, Int>>
)
