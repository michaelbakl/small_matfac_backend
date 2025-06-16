package app.web.controller

import app.core.model.game.AnswerResult
import app.core.service.game.IGameService
import app.web.model.request.game.AnswerSubmissionRequest
import app.web.model.request.game.CreateGameRequest
import app.web.model.request.game.UpdateDatesInGameRequest
import app.web.security.AuthRolesRequired
import app.web.security.UserCredentials
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.baklykov.app.core.converter.datetime.ZonedDateConverter
import app.core.converter.game.DifficultyConverter
import app.core.converter.game.GameTypeConverter
import app.core.util.CommonResponse
import ru.baklykov.app.core.converter.util.IdConverter
import app.core.model.game.GameConfig
import ru.baklykov.app.core.validation.ValidUUID
import app.web.model.response.game.GetGameInfoResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import java.util.*

@Tag(
    name = "Игры",
    description = "Контроллер для управления играми: создание, запуск, добавление вопросов и ответов"
)
@RestController
@RequestMapping("/api/games")
class GameController(
    private val gameService: IGameService
) {

    @Operation(
        summary = "Создание игры",
        description = "Создаёт новую игру в указанной комнате. Доступно для преподавателей и администраторов.",
        responses = [
            ApiResponse(responseCode = "200", description = "Игра успешно создана"),
            ApiResponse(responseCode = "400", description = "Ошибка валидации запроса")
        ]
    )
    @PostMapping
    @AuthRolesRequired(value = ["TEACHER", "ADMIN"])
    fun createGame(
        @RequestAttribute("userCredentialsAttr") credentials: UserCredentials,
        @RequestParam @ValidUUID roomId: UUID,
        @RequestBody request: CreateGameRequest
    ): ResponseEntity<CommonResponse<GetGameInfoResponse>> {
        val gameConfig = GameConfig(
            questionCount = request.questionCount ?: 30,
            duration = request.duration ?: 30,
            gameType = GameTypeConverter.convert(request.gameType ?: "single"),
            difficulty = DifficultyConverter.convert(request.difficulty ?: "easy"),
            allowSkips = request.allowSkips,
            enableHints = request.enableHints,
            name = request.name,
            questions = IdConverter.convertToSecond(request.questions),
            startDate = ZonedDateConverter.convert(request.startDate),
            finishDate = ZonedDateConverter.convert(request.finishDate)
        )
        val response = gameService.createGame(
            creatorId = credentials.userId,
            roomId = roomId.toString(),
            name = request.name,
            gameConfig = gameConfig,
            categories = request.categories?.joinToString(",") ?: ""
        )

        return ResponseEntity.ok(CommonResponse(response = response))
    }

    @Operation(
        summary = "Запуск игры",
        description = "Запускает игру в указанной комнате. Доступно для модераторов и преподавателей.",
        responses = [
            ApiResponse(responseCode = "200", description = "Игра запущена"),
            ApiResponse(responseCode = "404", description = "Игра не найдена")
        ]
    )
    @PostMapping("/{gameId}/start")
    @AuthRolesRequired("TEACHER", "MODERATOR", "ADMIN")
    fun startGameInRoom(
        @RequestParam @ValidUUID roomId: UUID,
        @PathVariable @ValidUUID gameId: UUID
    ): ResponseEntity<CommonResponse<GetGameInfoResponse>> {
        return ResponseEntity.ok(
            CommonResponse(
                response = gameService.startGameInRoom(
                    roomId.toString(),
                    gameId.toString()
                )
            )
        )
    }

    @Operation(
        summary = "Добавление вопросов в игру",
        description = "Добавляет список вопросов в уже существующую игру.",
        responses = [
            ApiResponse(responseCode = "200", description = "Вопросы успешно добавлены"),
            ApiResponse(responseCode = "400", description = "Ошибка при добавлении вопросов")
        ]
    )
    @PostMapping("/{gameId}/questions")
    @AuthRolesRequired("TEACHER", "MODERATOR", "ADMIN")
    fun addQuestionsToGame(
        @RequestParam @ValidUUID roomId: UUID,
        @PathVariable @ValidUUID gameId: UUID,
        @RequestBody questionIds: List<UUID>
    ): ResponseEntity<CommonResponse<GetGameInfoResponse>> {
        val response =
            gameService.addQuestionsToTheGame(roomId.toString(), gameId.toString(), questionIds.map { it.toString() })
        return ResponseEntity.ok(CommonResponse(response = response))
    }

    @Operation(
        summary = "Обновление даты начала и окончания игры",
        description = "Позволяет изменить время начала и завершения игры.",
        responses = [
            ApiResponse(responseCode = "200", description = "Даты успешно обновлены")
        ]
    )
    @PutMapping("/{gameId}/dates")
    @AuthRolesRequired("TEACHER", "MODERATOR", "ADMIN")
    fun updateGameDates(
        @RequestParam @ValidUUID roomId: UUID,
        @PathVariable @ValidUUID gameId: UUID,
        @RequestBody request: UpdateDatesInGameRequest
    ): ResponseEntity<CommonResponse<GetGameInfoResponse>> {
        val start = request.startDate.takeIf { it.isNotBlank() }?.let { ZonedDateConverter.convert(it) }
        val finish = request.finishDate.takeIf { it.isNotBlank() }?.let { ZonedDateConverter.convert(it) }
        return ResponseEntity.ok(
            CommonResponse(
                response = gameService.changeGameDates(
                    roomId.toString(),
                    gameId.toString(),
                    start,
                    finish
                )
            )
        )
    }

    @Operation(
        summary = "Получение информации об игре",
        description = "Возвращает полную информацию об игре по ID.",
        responses = [
            ApiResponse(responseCode = "200", description = "Информация об игре получена"),
            ApiResponse(responseCode = "404", description = "Игра не найдена")
        ]
    )
    @GetMapping("/{gameId}")
    fun getGameInfo(
        @RequestParam @ValidUUID roomId: UUID,
        @PathVariable @ValidUUID gameId: UUID
    ): ResponseEntity<CommonResponse<GetGameInfoResponse>> {
        return ResponseEntity.ok(
            CommonResponse(
                response = gameService.getGameById(
                    roomId.toString(),
                    gameId.toString()
                )
            )
        )
    }

    @Operation(
        summary = "Удаление игры",
        description = "Удаляет игру по ID. Доступно для преподавателей и администраторов.",
        responses = [
            ApiResponse(responseCode = "204", description = "Игра удалена"),
            ApiResponse(responseCode = "404", description = "Игра не найдена")
        ]
    )
    @DeleteMapping("/{gameId}")
    @AuthRolesRequired("TEACHER", "ADMIN")
    fun deleteGame(
        @PathVariable @ValidUUID gameId: UUID
    ): ResponseEntity<CommonResponse<Void>> {
        val deleted = gameService.deleteGameById(gameId.toString())
        return if (deleted > 0) ResponseEntity.noContent().build()
        else ResponseEntity.notFound().build()
    }

    @Operation(
        summary = "Старт игры для студента",
        description = "Позволяет студенту начать участие в выбранной игре.",
        responses = [
            ApiResponse(responseCode = "200", description = "Игра успешно начата"),
            ApiResponse(responseCode = "403", description = "Нет доступа")
        ]
    )
    @PostMapping("/{gameId}/start-student")
    @AuthRolesRequired("STUDENT")
    fun startGameForStudent(
        @RequestAttribute("userCredentialsAttr") credentials: UserCredentials,
        @PathVariable @ValidUUID gameId: UUID
    ): ResponseEntity<CommonResponse<Boolean>> {
        val success = gameService.startGameForStudent(gameId, UUID.fromString(credentials.userId))
        return ResponseEntity.ok(CommonResponse(response = success))
    }

    @Operation(
        summary = "Отправка ответа на вопрос",
        description = "Позволяет студенту отправить ответ на вопрос в рамках игры.",
        responses = [
            ApiResponse(responseCode = "200", description = "Ответ принят и оценён"),
            ApiResponse(responseCode = "400", description = "Некорректный запрос")
        ]
    )
    @PostMapping("/{gameId}/submit")
    @AuthRolesRequired("STUDENT")
    fun submitAnswer(
        @RequestAttribute("userCredentialsAttr") credentials: UserCredentials,
        @PathVariable @ValidUUID gameId: UUID,
        @RequestBody answer: AnswerSubmissionRequest
    ): ResponseEntity<CommonResponse<AnswerResult>> {
        val result = gameService.submitAnswer(gameId, UUID.fromString(credentials.userId), answer)
        return ResponseEntity.ok(CommonResponse(response = result))
    }
}

