package app.web.model.response.person

import app.core.model.person.StudentGroupInfo
import io.swagger.v3.oas.annotations.media.Schema
import java.time.ZonedDateTime
import java.util.*

@Schema(description = "Информация о студенте")
data class GetStudentInfoResponse(

    @field:Schema(description = "Уникальный идентификатор студента", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    val studentId: UUID,

    @field:Schema(description = "Уникальный идентификатор пользователя", example = "7fa85f64-5717-4562-b3fc-2c963f66afa7")
    val userId: UUID,

    @field:Schema(description = "Фамилия студента", example = "Иванов")
    val surname: String,

    @field:Schema(description = "Имя студента", example = "Иван")
    val name: String,

    @field:Schema(description = "Отчество студента", example = "Иванович", nullable = true)
    val middleName: String?,

    @field:Schema(description = "Электронная почта", example = "ivanov@example.com")
    val email: String,

    @field:Schema(description = "Дата рождения", example = "2000-01-01T00:00:00Z", nullable = true)
    val dateOfBirth: ZonedDateTime? = null,

    @field:Schema(description = "Дата поступления", example = "2018-09-01T00:00:00Z", nullable = true)
    var dateOfEntering: ZonedDateTime? = null,

    @field:Schema(description = "Список групп студента")
    val groups: List<StudentGroupInfo> = listOf()
)
