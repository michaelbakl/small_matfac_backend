package app.web.model.request.login

import com.fasterxml.jackson.annotation.JsonProperty

data class RefreshTokensRequest (

    @JsonProperty("refreshToken")
    val refreshToken: String

)