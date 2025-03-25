package app.web.model.response.person

import com.fasterxml.jackson.annotation.JsonProperty

class GroupResponse (

    @JsonProperty
    val groupId: String,

    @JsonProperty
    val name: String
)
