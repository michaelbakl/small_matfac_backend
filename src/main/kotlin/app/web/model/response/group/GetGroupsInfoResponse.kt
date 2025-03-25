package app.web.model.response.group

import com.fasterxml.jackson.annotation.JsonProperty

data class GetGroupsInfoResponse (

    @JsonProperty
    val groups: List<GetGroupInfoResponse>

)
