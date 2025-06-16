package app.web.model.response.room

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "Информация о комнате")
data class GetRoomInfoResponse(

    @field:Schema(description = "Уникальный идентификатор комнаты", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    val roomId: UUID,

    @field:Schema(description = "Название комнаты", example = "Математика - 101")
    val name: String,

    @field:Schema(description = "Идентификатор учителя", example = "7fa85f64-5717-4562-b3fc-2c963f66afa7")
    val teacherId: UUID,

    @field:Schema(description = "Список идентификаторов студентов")
    val students: List<UUID>,

    @field:Schema(description = "Статус закрытия комнаты")
    val isClosed: Boolean,

    @field:Schema(description = "Список идентификаторов игр, связанных с комнатой")
    val games: List<UUID>,

    @field:Schema(description = "Дата создания комнаты", example = "2023-06-01T12:00:00Z")
    val dateOfCreating: String
)
