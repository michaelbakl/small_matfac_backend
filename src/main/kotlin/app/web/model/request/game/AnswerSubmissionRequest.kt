package app.web.model.request.game

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.util.*

@Schema(description = "Запрос на отправку ответа на вопрос")
data class AnswerSubmissionRequest(

    @field:NotNull
    @Schema(description = "Идентификатор вопроса", example = "c9b1a38e-b52f-4f1a-87f6-9b9c9d5c1234", required = true)
    val questionId: UUID,

    @Schema(description = "Выбранный вариант ответа (если применимо)", example = "c4b1a38e-b52f-4f1a-87f6-9b9c9d5c7890")
    val selectedOptionId: UUID? = null,

    @Schema(description = "Введённый текстовый ответ (если применимо)", example = "Paris")
    val typedAnswer: String? = null
)
