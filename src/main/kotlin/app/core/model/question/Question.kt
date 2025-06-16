package app.core.model.question

import app.web.model.dto.answer.AnswerDto
import java.util.*

/**
 * @param questionId - id
 * @param title - title of the question
 * @param pictures - pictures of the question, can be empty
 * @param description - question text
 * @param answers - map answer id -> pair right or wrong and points
 * @param themes - themes of question
 */
data class Question (
    val questionId: UUID,
    val ownerId: UUID,
    val title: String,
    val type: String,
    val pictures: List<UUID>,
    val description: String,
    val answers: List<AnswerDto>,
    val themes: List<QuestionTheme> = emptyList()
)
