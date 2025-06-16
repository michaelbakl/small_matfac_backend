package app.web.model.request.login

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "Запрос на вход пользователя")
data class SignInRequest(

    @Schema(description = "Почта", example = "john@example.com")
    @field:Size(min = 5, max = 50, message = "Почта должно содержать от 5 до 50 символов")
    @field:NotBlank(message = "Почта не может быть пустой")
    @JsonProperty("email")
    val email: String,

    @Schema(description = "Пароль", example = "my_1secret1_password")
    @field:Size(min = 8, max = 255, message = "Длина пароля должна быть от 8 до 255 символов")
    @field:NotBlank(message = "Пароль не может быть пустым")
    @JsonProperty("password")
    val password: String
)