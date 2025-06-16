package app.web.model.response.answer

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "Ответ на вопрос")
data class AnswerResponse(
    @field:Schema(description = "Уникальный идентификатор ответа", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    val id: UUID,

    @field:Schema(description = "Текст ответа", example = "42")
    val text: String,

    @field:Schema(description = "Является ли ответ правильным", example = "true")
    val correct: Boolean,

    @field:Schema(description = "Количество баллов за правильный ответ", example = "10")
    val points: Int
)
