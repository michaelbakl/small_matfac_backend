package app.web.model.request.subject

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*


data class AddSubjectRequest(

    @field:Schema(description = "Название предмета", example = "Математика")
    @field:NotEmpty(message = "Name cannot be empty")
    @field:Size(min = 2, max = 50, message = "Name should be between 2 and 30 characters")
    @JsonProperty
    val name: String,

    @field:Schema(
        description = "Дата актуальности в формате yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
        example = "2025-06-09T12:00:00.000000"
    )
    @field:NotEmpty(message = "Date can not be empty")
    @field:Pattern(
        regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{6}\$",
        message = "Date must match yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
    )
    @JsonProperty
    val actualityDate: String
)