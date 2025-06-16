package app.web.model.response.theme

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Иерархия темы")
data class ThemeHierarchyResponse(
    @field:Schema(description = "Информация о теме")
    val theme: ThemeResponse,

    @field:Schema(description = "Дочерние темы")
    val children: List<ThemeHierarchyResponse>
)
