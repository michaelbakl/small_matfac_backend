package app.web.model.dto.answer

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

@Schema(description = "Ответ на вопрос")
data class AnswerDto(

    @Schema(description = "Идентификатор ответа", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    val id: UUID? = null,

    @field:NotBlank
    @field:Size(max = 500)
    @Schema(description = "Текст ответа", example = "42", required = true)
    val text: String,

    @Schema(description = "Является ли ответ правильным", example = "true", required = true)
    val correct: Boolean,

    @field:Min(0)
    @Schema(description = "Количество баллов за ответ", example = "10", required = true)
    val points: Int
)
