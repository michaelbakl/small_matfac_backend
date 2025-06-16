package app.web.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Запрос для входа пользователя")
data class Login (

    @JsonProperty("login")
    @field:Schema(description = "Логин пользователя", example = "john_doe")
    val login: String,

    @JsonProperty("password")
    @field:Schema(description = "Пароль пользователя", example = "my_secret_password")
    val password: String

)
