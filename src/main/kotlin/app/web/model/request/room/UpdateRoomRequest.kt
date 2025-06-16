package app.web.model.request.room

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import java.util.*

data class UpdateRoomRequest(

    @field:Schema(description = "Название комнаты", example = "Math group")
    @field:NotEmpty(message = "Name cannot be empty or null")
    @JsonProperty
    val name: String,

    @field:Schema(description = "UUID учителя", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    @field:NotEmpty(message = "Id cannot be empty")
    @field:Pattern(
        regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$",
        message = "Invalid UUID format"
    )
    @JsonProperty
    val teacherId: String,

    @field:Schema(description = "Список UUID групп")
    @JsonProperty
    val groups: List<UUID>,

    @field:Schema(description = "Флаг закрытости комнаты", example = "true")
    @JsonProperty
    val isClosed: Boolean,

    @field:Schema(description = "Список UUID участников")
    @JsonProperty
    val participants: List<String>,
)
