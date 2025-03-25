package app.web.model.request.student

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import ru.baklykov.app.core.model.person.StudentGroupInfo
import java.util.*

data class AddStudentRequest (

    var userId: UUID? = null,

    var studentId: UUID? = null,

    @NotEmpty(message = "Surname can not be empty")
    @JsonProperty("surname")
    val surname: String,

    @NotEmpty(message = "Name can not be empty")
    @JsonProperty("name")
    val name: String,

    @NotEmpty(message = "Middle name can not be empty")
    @JsonProperty("middlename")
    val middleName: String?,

    @NotEmpty(message = "Email can not be empty")
    @JsonProperty("username")
    val email: String,

    @NotEmpty(message = "Password can not be empty")
    @JsonProperty("password")
    val password: String,

    @NotEmpty(message = "Date can not be empty")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{6}\$")
    @JsonProperty("dateOfBirth")
    val dateOfBirth: String? = null,

    @NotEmpty(message = "Date can not be empty")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{6}\$")
    @JsonProperty("dateOfEntering")
    val dateOfEntering: String? = null,

    @JsonProperty("groups")
    val groups: List<StudentGroupInfo>

)