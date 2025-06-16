package app.web.model.request.subject

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*

data class GetSubjectsWithParamsRequest(

    @field:Schema(description = "UUID предмета", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", nullable = true)
    @field:Pattern(
        regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$",
        message = "Invalid UUID format"
    )
    @JsonProperty
    val id: String?,

    @field:Schema(description = "Название предмета", example = "Физика", nullable = true)
    @field:Size(min = 2, max = 30, message = "Name should be between 2 and 30 characters")
    @JsonProperty
    val name: String?
)
