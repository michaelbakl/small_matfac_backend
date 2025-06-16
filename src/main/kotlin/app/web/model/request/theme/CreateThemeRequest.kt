package app.web.model.request.theme

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class CreateThemeRequest(

    @field:Schema(description = "Название темы", example = "Математика", required = true)
    @field:NotBlank(message = "Name cannot be blank")
    @JsonProperty
    val name: String,

    @field:Schema(description = "ID родительской темы (если есть)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", nullable = true)
    @JsonProperty
    val parentId: String? = null
)
