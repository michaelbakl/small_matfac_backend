package app.web.model.request.subject

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.*

data class GetSubjectsWithParamsRequest (

    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$")
    @JsonProperty
    val id: String?,

    @Size(min = 2, max = 30, message = "Name should be between 2 and 30 characters")
    @JsonProperty
    val name: String?,

)
