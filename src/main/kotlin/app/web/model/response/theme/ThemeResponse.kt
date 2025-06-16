package app.web.model.response.theme

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "Информация о теме")
data class ThemeResponse(

    @field:Schema(description = "Уникальный идентификатор темы", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    val id: UUID,

    @field:Schema(description = "Название темы", example = "Программирование")
    val name: String,

    @field:Schema(description = "Путь темы", example = "Программирование/Кotlin/Классы")
    val path: String,

    @field:Schema(description = "Уровень вложенности темы", example = "2")
    val level: Int,

    @field:Schema(description = "Идентификатор родительской темы (если есть)", example = "7fa85f64-5717-4562-b3fc-2c963f66afa7")
    val parentId: UUID?,

    @field:Schema(description = "Флаг, есть ли дочерние темы", example = "true")
    val hasChildren: Boolean
)
