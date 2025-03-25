package app.web.model.request.login

import com.fasterxml.jackson.annotation.JsonProperty

data class SignUpRequest(

    @JsonProperty("email")
    val email: String,

    @JsonProperty("password")
    val password: String
)