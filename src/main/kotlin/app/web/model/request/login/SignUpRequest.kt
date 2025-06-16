package app.web.model.request.login

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "Запрос на простую регистрацию по email и паролю")
data class SignUpRequest(

    @field:NotBlank(message = "Email cannot be empty")
    @Schema(description = "Email", example = "newuser@example.com")
    @JsonProperty("email")
    val email: String,

    @field:NotBlank(message = "Password cannot be empty")
    @Schema(description = "Пароль", example = "NewUserPass123")
    @JsonProperty("password")
    val password: String
)
