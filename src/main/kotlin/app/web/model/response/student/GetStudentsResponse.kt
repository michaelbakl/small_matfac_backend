package app.web.model.response.student

import app.web.model.response.person.GetStudentInfoResponse
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

import java.util.*
@Schema(description = "Ответ с информацией о студентах")
data class GetStudentsResponse(

    @JsonProperty
    @field:Schema(description = "Сопоставление идентификатора студента и списка его информации")
    val students: Map<UUID, List<GetStudentInfoResponse>>
)
