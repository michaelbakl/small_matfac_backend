package app.web.model.request.student

import io.swagger.v3.oas.annotations.media.Schema


data class GetStudentWithParamsRequest(

    @field:Schema(description = "UUID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", nullable = true)
    val id: String?,

    @field:Schema(description = "UUID студента", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", nullable = true)
    val studentId: String?,

    @field:Schema(description = "Фамилия", example = "Иванов", nullable = true)
    val surname: String?,

    @field:Schema(description = "Имя", example = "Иван", nullable = true)
    val name: String?,

    @field:Schema(description = "Отчество", example = "Иванович", nullable = true)
    val middleName: String?,

    @field:Schema(description = "Email", example = "ivanov@example.com", nullable = true)
    val email: String?,

    @field:Schema(description = "Дата рождения в формате yyyy-MM-dd'T'HH:mm:ss.SSSSSS", example = "1990-05-15T00:00:00.000000", nullable = true)
    val dateOfBirth: String?,

    @field:Schema(description = "Дата поступления в формате yyyy-MM-dd'T'HH:mm:ss.SSSSSS", example = "2020-09-01T00:00:00.000000", nullable = true)
    val dateOfEntering: String?,

    @field:Schema(description = "Список UUID групп", nullable = true)
    val groups: List<String>?,
)
