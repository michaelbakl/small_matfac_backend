package app.web.model.request.theme

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

data class UpdateThemeRequest(

    @field:Schema(description = "Новое название темы", example = "Физика", required = true)
    @JsonProperty
    val name: String
)

