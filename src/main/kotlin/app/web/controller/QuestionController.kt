package app.web.controller

import app.core.util.CommonResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import ru.baklykov.app.core.service.question.IQuestionService
import ru.baklykov.app.core.service.question.IThemeService
import app.web.model.request.question.CreateQuestionRequest
import app.web.model.request.question.UpdateQuestionRequest
import app.web.security.AuthRolesRequired
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import app.web.model.response.question.GetQuestionInfoResponse
import java.util.*

@Tag(
    name = "Вопросы",
    description = "Управление вопросами, темами и поиском"
)
@RestController
@RequestMapping("/api/questions")
class QuestionController(
    private val questionService: IQuestionService,
    private val themeService: IThemeService
) {

    /**
     * Создание нового вопроса
     */
    @Operation(
        summary = "Создать новый вопрос",
        description = "Создаёт новый вопрос на платформе от имени преподавателя"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Вопрос успешно создан",
                content = [Content(schema = Schema(implementation = GetQuestionInfoResponse::class))]
            ),
            ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
        ]
    )
    @PostMapping
    @AuthRolesRequired("ADMIN", "MODERATOR","TEACHER")
    fun createQuestion(
        //@RequestAttribute("userCredentialsAttr") credentials: UserCredentials,
        @RequestBody request: CreateQuestionRequest,
    ): ResponseEntity<CommonResponse<GetQuestionInfoResponse>> {
        //val ownerId = credentials.userId
        val ownerId = "138dfa13-2901-4edb-b866-f27beeddba23"

        val createdQuestion = questionService.createQuestion(ownerId, request)
        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdQuestion.questionId)
            .toUri()

        return ResponseEntity.created(location).body(CommonResponse(response = createdQuestion))
    }

    /**
     * Получение вопроса по ID
     */
    @Operation(
        summary = "Получить вопрос по ID",
        parameters = [Parameter(name = "questionId", description = "UUID вопроса", required = true)],
        responses = [
            ApiResponse(responseCode = "200", description = "Информация о вопросе"),
            ApiResponse(responseCode = "404", description = "Вопрос не найден")
        ]
    )
    @AuthRolesRequired("ADMIN", "MODERATOR","TEACHER", "STUDENT")
    @GetMapping("/{questionId}")
    fun getQuestion(
        @PathVariable("questionId") questionId: UUID
    ): ResponseEntity<CommonResponse<GetQuestionInfoResponse?>> {
        return ResponseEntity.ok(CommonResponse(response = questionService.getQuestion(questionId)))
    }

    /**
     * Обновление вопроса
     */
    @Operation(
        summary = "Обновить существующий вопрос",
        description = "Позволяет изменить содержание вопроса"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Вопрос обновлён"),
            ApiResponse(responseCode = "404", description = "Вопрос не найден")
        ]
    )
    @PutMapping("/{questionId}")
    @AuthRolesRequired("ADMIN", "MODERATOR","TEACHER")
    fun updateQuestion(
        @PathVariable questionId: UUID,
        @RequestBody request: UpdateQuestionRequest
    ): ResponseEntity<CommonResponse<GetQuestionInfoResponse>> {
        return ResponseEntity.ok(CommonResponse(response = questionService.updateQuestion(questionId, request)))
    }

    /**
     * Удаление вопроса
     */
    @Operation(
        summary = "Удалить вопрос",
        description = "Удаляет вопрос по его идентификатору"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Вопрос успешно удалён"),
            ApiResponse(responseCode = "404", description = "Вопрос не найден")
        ]
    )
    @DeleteMapping("/{questionId}")
    @AuthRolesRequired("ADMIN", "MODERATOR")
    fun deleteQuestion(
        @PathVariable questionId: UUID
    ): ResponseEntity<CommonResponse<Void>> {
        questionService.deleteQuestion(questionId)
        return ResponseEntity.noContent().build()
    }

    /**
     * Поиск вопросов по названию
     */
    @Operation(
        summary = "Поиск вопросов",
        description = "Поиск вопросов по названию"
    )
    @GetMapping("/search")
    @AuthRolesRequired("ADMIN", "MODERATOR","TEACHER", "STUDENT")
    fun searchQuestions(
        @RequestParam("query") query: String,
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<CommonResponse<Page<GetQuestionInfoResponse?>>> {
        return ResponseEntity.ok(CommonResponse(response = questionService.searchQuestions(query, pageable)))
    }

    /**
     * Получение вопросов по теме
     */
    @Operation(
        summary = "Получить вопросы по теме",
        parameters = [
            Parameter(name = "themeId", description = "UUID темы", required = true)
        ]
    )
    @GetMapping("/by-theme/{themeId}")
    @AuthRolesRequired("ADMIN", "MODERATOR","TEACHER", "STUDENT")
    fun getQuestionsByTheme(
        @PathVariable themeId: UUID,
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<CommonResponse<Page<GetQuestionInfoResponse?>>> {
        return ResponseEntity.ok(
            CommonResponse(
                response = questionService.findByThemes(listOf(themeId), pageable.pageSize, pageable.pageNumber)
            )
        )
    }

    /**
     * Добавление темы к вопросу
     */
    @Operation(
        summary = "Добавить тему к вопросу",
        parameters = [
            Parameter(name = "questionId", description = "UUID вопроса", required = true),
            Parameter(name = "themeId", description = "UUID темы", required = true)
        ]
    )
    @PostMapping("/{questionId}/themes/{themeId}")
    @AuthRolesRequired("ADMIN", "MODERATOR","TEACHER")
    fun addThemeToQuestion(
        @PathVariable questionId: String,
        @PathVariable themeId: String
    ): ResponseEntity<CommonResponse<Void>> {
        themeService.addThemeToQuestion(UUID.fromString(questionId), UUID.fromString(themeId))
        return ResponseEntity.noContent().build()
    }

    /**
     * Удаление темы у вопроса
     */
    @Operation(
        summary = "Удалить тему у вопроса",
        parameters = [
            Parameter(name = "questionId", description = "UUID вопроса", required = true),
            Parameter(name = "themeId", description = "UUID темы", required = true)
        ]
    )
    @DeleteMapping("/{questionId}/themes/{themeId}")
    @AuthRolesRequired("ADMIN", "MODERATOR","TEACHER")
    fun removeThemeFromQuestion(
        @PathVariable questionId: String,
        @PathVariable themeId: String
    ): ResponseEntity<Void> {
        themeService.removeThemeFromQuestion(UUID.fromString(questionId), UUID.fromString(themeId))
        return ResponseEntity.noContent().build()
    }

}