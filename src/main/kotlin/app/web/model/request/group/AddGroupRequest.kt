package app.web.model.request.group

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*

@Schema(description = "Запрос на создание новой группы")
data class AddGroupRequest(

    @field:NotEmpty(message = "Name cannot be empty")
    @field:Size(min = 2, max = 20, message = "Name should be between 2 and 20 characters")
    @Schema(description = "Название группы", example = "Группа А1")
    @JsonProperty
    val name: String,

    @field:NotEmpty(message = "Date can not be empty")
    @field:Pattern(
        regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{6}$",
        message = "Date must be in ISO 8601 format with microseconds"
    )
    @Schema(
        description = "Дата создания группы (ISO 8601 с микросекундами)",
        example = "2025-06-08T14:30:00.123456"
    )
    @JsonProperty
    val dateOfCreating: String,

    @field:Min(1)
    @field:Max(11)
    @Schema(description = "Номер класса (1–11)", example = "10")
    @JsonProperty
    val classNum: Int
)
