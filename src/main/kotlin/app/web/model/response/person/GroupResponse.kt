package app.web.model.response.person

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Информация о группе")
class GroupResponse(

    @field:Schema(description = "Уникальный идентификатор группы", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    @JsonProperty
    val groupId: String,

    @field:Schema(description = "Название группы", example = "Группа А")
    @JsonProperty
    val name: String
)
