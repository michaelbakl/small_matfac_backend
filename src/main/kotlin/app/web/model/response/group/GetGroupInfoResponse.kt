package app.web.model.response.group

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.util.*

data class GetGroupInfoResponse (

    @JsonProperty
    val groupId: UUID,

    @JsonProperty
    val name: String,

    @JsonProperty
    val dateOfCreating: LocalDateTime,

    @JsonProperty
    val classNum: Int

)
