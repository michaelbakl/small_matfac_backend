package ru.baklykov.app.web.controller

import app.core.util.CommonResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import ru.baklykov.app.core.model.question.QuestionTheme
import ru.baklykov.app.core.service.question.IThemeService
import ru.baklykov.app.web.model.request.theme.CreateThemeRequest
import ru.baklykov.app.web.model.request.theme.UpdateThemeRequest
import ru.baklykov.app.web.model.response.ApiMessageResponse
import ru.baklykov.app.web.model.response.theme.ThemeHierarchyResponse
import ru.baklykov.app.web.model.response.theme.ThemeResponse
import java.util.UUID

@RestController
@RequestMapping("/themes")
class ThemeController(
    private val themeService: IThemeService
) {

    @PostMapping
    suspend fun createTheme(
        @Valid @RequestBody request: CreateThemeRequest
    ): CommonResponse<ThemeResponse> {
        val theme = themeService.createTheme(
            name = request.name,
            parentThemeId = request.parentId
        )
        return CommonResponse(response = themeService.convertToResponse(theme))
    }

    @GetMapping("/{themeId}")
    suspend fun getTheme(
        @PathVariable themeId: UUID
    ): CommonResponse<ThemeResponse> {
        return CommonResponse(response = themeService.convertToResponse(themeService.getTheme(themeId)))
    }

    // 3. Обновление темы
    @PutMapping("/{themeId}")
    suspend fun updateTheme(
        @PathVariable themeId: UUID,
        @RequestBody request: UpdateThemeRequest
    ): ThemeResponse {
        return themeService.convertToResponse(
            themeService.updateTheme(
                themeId = themeId,
                newName = request.name
            )
        )
    }

    // 4. Удаление темы
    @DeleteMapping("/{themeId}")
    suspend fun deleteTheme(
        @PathVariable themeId: UUID
    ): ApiMessageResponse {
        val success = themeService.deleteTheme(themeId)
        return ApiMessageResponse(
            success = success,
            message = if (success) "Theme deleted" else "Theme not found or has children"
        )
    }

    // 5. Получение иерархии тем
//    @GetMapping("/hierarchy")
//    suspend fun getThemeHierarchy(
//        @RequestParam(required = false) rootThemeId: UUID?
//    ): CommonResponse<List<QuestionTheme>> {
//        return CommonResponse(response = themeService.convertToResponse(themeService.getThemeHierarchy(rootThemeId).map { it.toResponse() })
//    }

    // 6. Поиск тем
    @GetMapping("/search")
    suspend fun searchThemes(
        @RequestParam query: String,
        @RequestParam(defaultValue = "20") limit: Int
    ): CommonResponse<List<ThemeResponse>> {
        val themes = themeService.searchThemes(query, limit).map { themeService.convertToResponse(it) }
        return CommonResponse(response = themes)
    }

    // 7. Управление связями с вопросами
    @PostMapping("/{themeId}/questions/{questionId}")
    suspend fun addThemeToQuestion(
        @PathVariable themeId: UUID,
        @PathVariable questionId: UUID
    ): CommonResponse<ApiMessageResponse> {
        themeService.addThemeToQuestion(questionId, themeId)
        return CommonResponse(response = ApiMessageResponse(success = true, message = "Theme added to question"))
    }

    @DeleteMapping("/{themeId}/questions/{questionId}")
    suspend fun removeThemeFromQuestion(
        @PathVariable themeId: UUID,
        @PathVariable questionId: UUID
    ): ApiMessageResponse {
        themeService.removeThemeFromQuestion(questionId, themeId)
        return ApiMessageResponse(success = true, message = "Theme removed from question")
    }
}