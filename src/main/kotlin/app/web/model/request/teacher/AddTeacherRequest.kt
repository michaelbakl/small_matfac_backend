package app.web.model.request.teacher

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.util.*

data class AddTeacherRequest (

    var userId: UUID? = null,

    var teacherId: UUID? = null,

    @NotEmpty(message = "Surname cannot be empty")
    @Size(min = 2, max = 50, message = "Surname should be between 2 and 30 characters")
    @JsonProperty("surname")
    val surname: String,

    @NotEmpty(message = "Name cannot be empty")
    @Size(min = 2, max = 50, message = "Name should be between 2 and 30 characters")
    @JsonProperty("name")
    val name: String,

    @NotEmpty(message = "Middle name cannot be empty")
    @Size(min = 2, max = 50, message = "Middle name should be between 2 and 30 characters")
    @JsonProperty("middlename")
    val middleName: String?,

    @NotEmpty(message = "Email cannot be empty")
    @Size(min = 2, max = 50, message = "Email should be between 2 and 30 characters")
    @JsonProperty("username")
    val email: String,

    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 2, max = 50, message = "Password should be between 2 and 30 characters")
    @JsonProperty("password")
    val password: String,

    @NotEmpty(message = "Date can not be empty")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{6}\$")
    @JsonProperty("dateOfBirth")
    val dateOfBirth: String? = null

)