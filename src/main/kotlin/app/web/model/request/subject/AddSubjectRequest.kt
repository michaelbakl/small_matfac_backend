package app.web.model.request.subject

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.*

data class AddSubjectRequest(

    @NotEmpty(message = "Name cannot be empty")
    @Size(min = 2, max = 50, message = "Name should be between 2 and 30 characters")
    @JsonProperty
    val name: String,

    @NotEmpty(message = "Date can not be empty")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{6}\$")
    @JsonProperty
    val actualityDate: String
)