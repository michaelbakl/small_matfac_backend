package app.web.model.request.question

import app.web.model.dto.answer.AnswerDto
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "Request to create a new question")
data class CreateQuestionRequest(
    @field:Schema(description = "Title of the question", example = "What is Kotlin?")
    val title: String,

    @field:Schema(description = "Description of the question", example = "Explain what Kotlin programming language is.")
    val description: String,

    @field:Schema(description = "List of picture URLs associated with the question", example = "[\"http://example.com/image1.png\"]")
    val pictures: List<String> = emptyList(),

    @field:Schema(description = "List of answers for the question")
    val answers: List<AnswerDto>,

    @field:Schema(description = "List of theme UUIDs related to the question", example = "[\"3fa85f64-5717-4562-b3fc-2c963f66afa6\"]", nullable = true)
    val themeIds: List<UUID>? = null,

    @field:Schema(description = "Type of the question", example = "multiple_choice")
    val type: String
)
