package app.web.model.response.subject

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Информация по предмету")
data class GetSubjectResponse(

    @JsonProperty
    @field:Schema(description = "Уникальный идентификатор предмета", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    val id: String,

    @JsonProperty
    @field:Schema(description = "Название предмета", example = "Математика")
    val name: String
)
