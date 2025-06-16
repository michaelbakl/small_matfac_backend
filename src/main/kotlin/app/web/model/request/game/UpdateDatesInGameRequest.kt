package app.web.model.request.game

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "Запрос на обновление дат начала и окончания игры")
data class UpdateDatesInGameRequest(

    @field:NotBlank
    @Schema(description = "Новая дата начала игры (ISO 8601 формат)", example = "2025-06-08T12:00:00Z", required = true)
    val startDate: String = "",

    @field:NotBlank
    @Schema(description = "Новая дата окончания игры (ISO 8601 формат)", example = "2025-06-08T13:00:00Z", required = true)
    val finishDate: String = ""
)
