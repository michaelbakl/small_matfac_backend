package app.web.model.response.login

import com.fasterxml.jackson.annotation.JsonProperty

data class SignInResponse (

    @JsonProperty("accessToken")
    val accessToken: String,

    @JsonProperty("refreshToken")
    val refreshToken: String,

)