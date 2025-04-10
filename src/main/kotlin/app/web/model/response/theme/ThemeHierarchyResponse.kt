package ru.baklykov.app.web.model.response.theme

data class ThemeHierarchyResponse(
    val theme: ThemeResponse,
    val children: List<ThemeHierarchyResponse>
)
