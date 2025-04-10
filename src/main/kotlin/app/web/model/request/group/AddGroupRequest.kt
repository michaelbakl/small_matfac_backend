package app.web.model.request.group

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.*

data class AddGroupRequest (

    @NotEmpty(message = "Name cannot be empty")
    @Size(min = 2, max = 20, message = "Name should be between 2 and 20 characters")
    @JsonProperty
    val name: String,

    @NotEmpty(message = "Date can not be empty")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{6}\$")
    @JsonProperty
    val dateOfCreating: String,

    @Min(1)
    @Max(11)
    @JsonProperty
    val classNum: Int

)
