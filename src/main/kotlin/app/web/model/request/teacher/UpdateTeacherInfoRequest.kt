package app.web.model.request.teacher

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UpdateTeacherInfoRequest(

    @field:Schema(description = "UUID пользователя", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    @field:NotEmpty(message = "Id cannot be empty")
    @field:Pattern(
        regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$",
        message = "Invalid UUID format"
    )
    @JsonProperty("userId")
    var userId: String,

    @field:Schema(description = "UUID преподавателя", example = "4fa85f64-5717-4562-b3fc-2c963f66afa7")
    @field:NotEmpty(message = "Id cannot be empty")
    @field:Pattern(
        regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$",
        message = "Invalid UUID format"
    )
    @JsonProperty("teacherId")
    var teacherId: String,

    @field:Schema(description = "Фамилия", example = "Иванов")
    @field:NotEmpty(message = "Surname cannot be empty")
    @field:Size(min = 2, max = 50, message = "Surname should be between 2 and 30 characters")
    @JsonProperty("surname")
    val surname: String,

    @field:Schema(description = "Имя", example = "Иван")
    @field:NotEmpty(message = "Name cannot be empty")
    @field:Size(min = 2, max = 50, message = "Name should be between 2 and 30 characters")
    @JsonProperty("name")
    val name: String,

    @field:Schema(description = "Отчество", example = "Иванович", nullable = true)
    @field:NotEmpty(message = "Middle name cannot be empty")
    @field:Size(min = 2, max = 50, message = "Middle name should be between 2 and 30 characters")
    @JsonProperty("middlename")
    val middleName: String?,

    @field:Schema(description = "Email", example = "ivanov@example.com")
    @field:NotEmpty(message = "Email cannot be empty")
    @field:Size(min = 2, max = 50, message = "Email should be between 2 and 30 characters")
    @JsonProperty("email")
    val email: String,

    @field:Schema(
        description = "Дата рождения в формате yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
        example = "1980-12-25T00:00:00.000000",
        nullable = true
    )
    @field:NotEmpty(message = "Date can not be empty")
    @field:Pattern(
        regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{6}\$",
        message = "Date must match yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
    )
    @JsonProperty("dateOfBirth")
    val dateOfBirth: String? = null,

    @field:Schema(
        description = "Дата изменения в формате yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
        example = "2025-06-09T12:00:00.000000"
    )
    @field:NotEmpty(message = "Date can not be empty")
    @field:Pattern(
        regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{6}\$",
        message = "Date must match yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
    )
    @JsonProperty("dateOfChanging")
    val dateOfChanging: String
)
