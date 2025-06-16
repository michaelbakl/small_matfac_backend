package app.web.controller

import app.core.exception.NotFoundException
import app.core.util.CommonResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import ru.baklykov.app.core.converter.util.IdConverter
import ru.baklykov.app.core.service.question.IThemeService
import ru.baklykov.app.core.validation.ValidUUID
import app.web.model.request.theme.CreateThemeRequest
import app.web.model.request.theme.UpdateThemeRequest
import app.web.model.response.ApiMessageResponse
import app.web.model.response.theme.ThemeResponse
import java.util.UUID

@Tag(name = "Theme API", description = "Управление темами и их связями с вопросами")
@RestController
@RequestMapping("/api/themes")
class ThemeController(
    private val themeService: IThemeService
) {

    @Operation(
        summary = "Создать тему",
        description = "Создаёт новую тему, возможно с родительской темой",
        responses = [
            ApiResponse(responseCode = "200", description = "Тема успешно создана"),
            ApiResponse(responseCode = "400", description = "Ошибка валидации запроса")
        ]
    )
    @PostMapping
    fun createTheme(
        @Valid @RequestBody request: CreateThemeRequest
    ): CommonResponse<ThemeResponse> {
        val theme = themeService.createTheme(
            name = request.name,
            parentThemeId = IdConverter.convertTo(request.parentId)
        )
        return CommonResponse(response = themeService.convertToResponse(theme))
    }

    @Operation(
        summary = "Получить тему по ID",
        description = "Возвращает тему по её UUID",
        responses = [
            ApiResponse(responseCode = "200", description = "Тема найдена"),
            ApiResponse(responseCode = "404", description = "Тема не найдена или ID невалиден")
        ]
    )
    @GetMapping("/{themeId}")
    fun getTheme(
        @PathVariable @ValidUUID themeId: String
    ): CommonResponse<ThemeResponse> {
        return CommonResponse(
            response = themeService.convertToResponse(
                themeService.getTheme(
                    IdConverter.convertTo(
                        themeId
                    ) ?: throw NotFoundException("Theme was not found by id")
                )
            )
        )
    }

    @Operation(
        summary = "Обновить тему",
        description = "Изменяет название темы по ID",
        responses = [
            ApiResponse(responseCode = "200", description = "Тема успешно обновлена"),
            ApiResponse(responseCode = "404", description = "Тема не найдена")
        ]
    )
    @PutMapping("/{themeId}")
    fun updateTheme(
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

    @Operation(
        summary = "Удалить тему",
        description = "Удаляет тему, если она не содержит дочерние темы",
        responses = [
            ApiResponse(responseCode = "200", description = "Тема удалена или не найдена"),
            ApiResponse(responseCode = "400", description = "Ошибка удаления")
        ]
    )
    @DeleteMapping("/{themeId}")
    fun deleteTheme(
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

    @Operation(
        summary = "Поиск тем",
        description = "Поиск тем по строке запроса, с возможностью ограничения количества",
        responses = [
            ApiResponse(responseCode = "200", description = "Темы найдены")
        ]
    )
    @GetMapping("/search")
    fun searchThemes(
        @RequestParam query: String,
        @RequestParam(defaultValue = "20", required = false) limit: Int
    ): CommonResponse<List<ThemeResponse>> {
        val themes = themeService.searchThemes(query, limit).map { themeService.convertToResponse(it) }
        return CommonResponse(response = themes)
    }

    @Operation(
        summary = "Добавить тему к вопросу",
        description = "Устанавливает связь между темой и вопросом",
        responses = [
            ApiResponse(responseCode = "200", description = "Связь успешно установлена")
        ]
    )
    @PostMapping("/{themeId}/questions/{questionId}")
    fun addThemeToQuestion(
        @PathVariable themeId: UUID,
        @PathVariable questionId: UUID
    ): CommonResponse<ApiMessageResponse> {
        themeService.addThemeToQuestion(questionId, themeId)
        return CommonResponse(response = ApiMessageResponse(success = true, message = "Theme added to question"))
    }

    @Operation(
        summary = "Удалить тему из вопроса",
        description = "Удаляет связь между темой и вопросом",
        responses = [
            ApiResponse(responseCode = "200", description = "Связь успешно удалена")
        ]
    )
    @DeleteMapping("/{themeId}/questions/{questionId}")
    fun removeThemeFromQuestion(
        @PathVariable themeId: UUID,
        @PathVariable questionId: UUID
    ): ApiMessageResponse {
        themeService.removeThemeFromQuestion(questionId, themeId)
        return ApiMessageResponse(success = true, message = "Theme removed from question")
    }
}
