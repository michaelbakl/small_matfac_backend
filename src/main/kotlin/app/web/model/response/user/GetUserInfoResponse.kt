package app.web.model.response.user

import com.fasterxml.jackson.annotation.JsonProperty

data class GetUserInfoResponse (

    @JsonProperty("userId")
    val userId: String,

    @JsonProperty("email")
    val email: String,

    @JsonProperty("roles")
    val roles: List<String>
)
