package app.web.model.request.login

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@Schema(description = "Запрос на регистрацию пользователя с полными данными")
data class SignUpRegistrationRequest (

    @field:NotEmpty(message = "Surname cannot be empty")
    @field:Size(min = 2, max = 50, message = "Surname should be between 2 and 50 characters")
    @Schema(description = "Фамилия", example = "Иванов")
    @JsonProperty("surname")
    val surname: String,

    @field:NotEmpty(message = "Name cannot be empty")
    @field:Size(min = 2, max = 50, message = "Name should be between 2 and 50 characters")
    @Schema(description = "Имя", example = "Иван")
    @JsonProperty("name")
    val name: String,

    @field:NotEmpty(message = "Middle name cannot be empty")
    @field:Size(min = 2, max = 50, message = "Middle name should be between 2 and 50 characters")
    @Schema(description = "Отчество", example = "Иванович")
    @JsonProperty("middlename")
    val middleName: String?,

    @field:NotEmpty(message = "Email cannot be empty")
    @field:Size(min = 5, max = 50, message = "Email should be between 5 and 50 characters")
    @Schema(description = "Email", example = "ivan@example.com")
    @JsonProperty("email")
    val email: String,

    @field:NotEmpty(message = "Password cannot be empty")
    @field:Size(min = 8, max = 50, message = "Password should be between 8 and 50 characters")
    @Schema(description = "Пароль", example = "securePassword123")
    @JsonProperty("password")
    val password: String,

    @field:NotEmpty(message = "Requested role cannot be empty")
    @field:Size(min = 2, max = 50, message = "Requested role should be between 2 and 50 characters")
    @Schema(description = "Роль пользователя", example = "student")
    @JsonProperty("role")
    val requestedRole: String,

    @field:NotEmpty(message = "Date cannot be empty")
    @field:Pattern(
        regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{6}$",
        message = "Date must be in ISO 8601 format with microseconds"
    )
    @Schema(description = "Дата рождения (ISO 8601)", example = "2000-01-01T00:00:00.000000")
    @JsonProperty("dateOfBirth")
    val dateOfBirth: String? = null
)