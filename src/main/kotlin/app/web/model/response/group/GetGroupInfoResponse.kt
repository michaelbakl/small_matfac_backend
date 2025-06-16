package app.web.model.response.group

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import java.time.ZonedDateTime
import java.util.*

@Schema(description = "Информация о группе")
data class GetGroupInfoResponse(

    @field:Schema(description = "Уникальный идентификатор группы", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    @JsonProperty
    val groupId: UUID,

    @field:Schema(description = "Название группы", example = "Группа А")
    @JsonProperty
    val name: String,

    @field:Schema(description = "Дата создания группы", example = "2025-06-09T12:00:00Z")
    @JsonProperty
    val dateOfCreating: ZonedDateTime,

    @field:Schema(description = "Номер класса", example = "10")
    @JsonProperty
    val classNum: Int
)
