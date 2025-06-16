package app.web.model.response.login

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Ответ при входе пользователя с токенами")
data class SignInResponse(

    @field:Schema(description = "Токен доступа", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNj...")
    @JsonProperty("accessToken")
    val accessToken: String,

    @field:Schema(description = "Токен обновления для доступа", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNj...")
    @JsonProperty("refreshToken")
    val refreshToken: String,
)
