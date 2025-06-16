package app.web.model.response.user

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Информация о пользователе")
data class GetUserInfoResponse(

    @JsonProperty("userId")
    @field:Schema(description = "Уникальный идентификатор пользователя", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    val userId: String,

    @JsonProperty("email")
    @field:Schema(description = "Email пользователя", example = "user@example.com")
    val email: String,

    @JsonProperty("roles")
    @field:Schema(description = "Список ролей пользователя", example = "[\"ADMIN\", \"USER\"]")
    val roles: List<String>
)
