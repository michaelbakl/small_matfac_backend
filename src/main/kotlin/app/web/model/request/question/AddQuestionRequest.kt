package app.web.model.request.question

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "Request to add a question by ID")
data class AddQuestionRequest (
    @field:Schema(description = "Unique identifier of the question", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    val id: UUID,
)