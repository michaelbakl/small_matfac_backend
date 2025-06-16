package app.web.model.request.group

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*

@Schema(description = "Запрос на обновление информации о группе")
data class UpdateGroupRequest(

    @field:NotEmpty(message = "Id cannot be empty")
    @field:Pattern(
        regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$",
        message = "Invalid UUID format"
    )
    @Schema(description = "Идентификатор группы", example = "123e4567-e89b-12d3-a456-426614174000")
    @JsonProperty
    val groupId: String,

    @field:NotEmpty(message = "Name cannot be empty")
    @field:Size(min = 2, max = 20, message = "Name should be between 2 and 20 characters")
    @Schema(description = "Новое название группы", example = "Группа Б2")
    @JsonProperty
    val name: String,

    @field:NotEmpty(message = "Date can not be empty")
    @field:Pattern(
        regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{6}$",
        message = "Date must be in ISO 8601 format with microseconds"
    )
    @Schema(
        description = "Дата изменения группы (ISO 8601 с микросекундами)",
        example = "2025-06-08T15:45:00.654321"
    )
    @JsonProperty
    val dateOfCreating: String,

    @field:NotEmpty(message = "Faculty ID cannot be empty")
    @field:Pattern(
        regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$",
        message = "Invalid UUID format"
    )
    @Schema(description = "Идентификатор факультета", example = "123e4567-e89b-12d3-a456-426614174001")
    @JsonProperty
    val facultyId: String,

    @field:Min(1)
    @field:Max(12)
    @Schema(description = "Номер семестра (1–12)", example = "2")
    @JsonProperty
    val semesterNum: Int
)
