package app.web.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Login (

    @JsonProperty("login")
    val login: String,

    @JsonProperty("password")
    val password: String

)
