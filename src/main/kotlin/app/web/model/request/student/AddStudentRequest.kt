package app.web.model.request.student

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import app.core.model.person.StudentGroupInfo
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class AddStudentRequest(

    @field:Schema(description = "UUID пользователя", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", nullable = true)
    @JsonProperty("userId")
    var userId: UUID? = null,

    @field:Schema(description = "UUID студента", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", nullable = true)
    @JsonProperty("studentId")
    var studentId: UUID? = null,

    @field:Schema(description = "Фамилия студента", example = "Иванов")
    @field:NotEmpty(message = "Surname can not be empty")
    @JsonProperty("surname")
    val surname: String,

    @field:Schema(description = "Имя студента", example = "Иван")
    @field:NotEmpty(message = "Name can not be empty")
    @JsonProperty("name")
    val name: String,

    @field:Schema(description = "Отчество студента", example = "Иванович", nullable = true)
    @JsonProperty("middlename")
    val middleName: String?,

    @field:Schema(description = "Email студента", example = "ivanov@example.com")
    @field:NotEmpty(message = "Email can not be empty")
    @JsonProperty("email")
    val email: String,

    @field:Schema(description = "Пароль студента", example = "my_secure_password")
    @field:NotEmpty(message = "Password can not be empty")
    @JsonProperty("password")
    val password: String,

    @field:Schema(description = "Дата рождения студента в формате yyyy-MM-dd'T'HH:mm:ss.SSSSSS", example = "1990-05-15T00:00:00.000000", nullable = true)
    @field:Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{6}\$", message = "Date must match yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    @JsonProperty("dateOfBirth")
    val dateOfBirth: String? = null,

    @field:Schema(description = "Дата поступления студента в формате yyyy-MM-dd'T'HH:mm:ss.SSSSSS", example = "2020-09-01T00:00:00.000000", nullable = true)
    @field:Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{6}\$", message = "Date must match yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    @JsonProperty("dateOfEntering")
    val dateOfEntering: String? = null,

    @field:Schema(description = "Список групп студента")
    @JsonProperty("groups")
    val groups: List<StudentGroupInfo>
)