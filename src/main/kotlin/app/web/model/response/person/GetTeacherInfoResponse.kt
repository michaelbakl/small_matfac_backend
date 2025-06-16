package app.web.model.response.person

import io.swagger.v3.oas.annotations.media.Schema
import java.time.ZonedDateTime
import java.util.*

@Schema(description = "Информация о преподавателе")
data class GetTeacherInfoResponse(

    @field:Schema(description = "Уникальный идентификатор преподавателя", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    val teacherId: UUID,

    @field:Schema(description = "Уникальный идентификатор пользователя", example = "7fa85f64-5717-4562-b3fc-2c963f66afa7")
    val userId: UUID,

    @field:Schema(description = "Фамилия преподавателя", example = "Петров")
    val surname: String,

    @field:Schema(description = "Имя преподавателя", example = "Пётр")
    val name: String,

    @field:Schema(description = "Отчество преподавателя", example = "Петрович", nullable = true)
    val middleName: String?,

    @field:Schema(description = "Электронная почта", example = "petrov@example.com")
    val email: String,

    @field:Schema(description = "Дата рождения", example = "1980-05-15T00:00:00Z", nullable = true)
    val dateOfBirth: ZonedDateTime? = null,

    @field:Schema(description = "Дата поступления", example = "2000-09-01T00:00:00Z", nullable = true)
    var dateOfEntering: ZonedDateTime? = null
)
