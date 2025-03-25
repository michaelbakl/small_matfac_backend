package app.web.model.response.subject

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class GetSubjectResponse(
    @JsonProperty
    val id: String,

    @JsonProperty
    val name: String

)
