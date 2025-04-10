package ru.baklykov.app.web.model.response.theme

import java.util.*

data class ThemeResponse(
    val id: UUID,
    val name: String,
    val path: String,
    val level: Int,
    val parentId: UUID?,
    val hasChildren: Boolean
)
