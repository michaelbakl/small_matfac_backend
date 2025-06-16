package app.web.model.response.group

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Список групп")
data class GetGroupsInfoResponse(

    @field:Schema(description = "Список групп")
    @JsonProperty
    val groups: List<GetGroupInfoResponse>
)
