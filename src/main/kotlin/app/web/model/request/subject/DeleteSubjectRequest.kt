package app.web.model.request.subject

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern

data class DeleteSubjectRequest(

    @field:Schema(description = "UUID предмета", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    @field:NotEmpty(message = "Id cannot be empty")
    @field:Pattern(
        regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$",
        message = "Invalid UUID format"
    )
    @JsonProperty
    val id: String
)