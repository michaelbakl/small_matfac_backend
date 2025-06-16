package app.web.controller

import app.core.service.game.IGameService
import app.core.util.CommonResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.baklykov.app.core.model.game.GameStatus
import ru.baklykov.app.core.service.room.IRoomService
import ru.baklykov.app.core.validation.ValidUUID
import java.util.*

@Tag(
    name = "Сессии игр",
    description = "Управление игровыми сессиями для студентов"
)
@RestController
@RequestMapping("/api/games/session")
class GameSessionController(
    private val roomService: IRoomService,
    private val gameService: IGameService
) {

    @Operation(
        summary = "Запустить сессию игры для студента",
        description = """
        Позволяет студенту начать игровую сессию в указанной комнате. 
        Перед запуском проверяется, что студент присутствует в комнате и игра активна.
    """,
        parameters = [
            Parameter(name = "gameId", description = "UUID игры", required = true),
            Parameter(name = "studentId", description = "UUID студента", required = true),
            Parameter(name = "roomId", description = "UUID комнаты", required = true)
        ],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Сессия успешно начата",
                content = [Content(schema = Schema(implementation = CommonResponse::class))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Студент не находится в комнате"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Игра не активна"
            ),
            ApiResponse(
                responseCode = "409",
                description = "Сессия уже существует или не может быть начата"
            )
        ]
    )
    @PostMapping("/start")
    fun startGameForStudent(
        @RequestParam @ValidUUID gameId: UUID,
        @RequestParam @ValidUUID studentId: UUID,
        @RequestParam @ValidUUID roomId: UUID
    ): ResponseEntity<CommonResponse<String>> {
        // 1. Проверка, что студент в комнате
        val isStudentInRoom = roomService.isUserInRoom(roomId.toString(), studentId.toString())
        if (!isStudentInRoom) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CommonResponse(isError = true, response = "Student is not in the room."))
        }

        // 2. Проверка, что игра активна и началась
        val gameInfo = gameService.getGameById(roomId.toString(), gameId.toString())
        if (gameInfo.status != GameStatus.CURRENTLY_PLAYED.name) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse(isError = true, response = "Game is not active."))
        }

        // 3. Запуск сессии
        val started = gameService.startGameForStudent(gameId, studentId)
        return if (started) {
            ResponseEntity.ok(CommonResponse(response = "Game session started."))
        } else {
            ResponseEntity.status(HttpStatus.CONFLICT).body(CommonResponse(isError = true, response = "Game session already exists or cannot start."))
        }
    }
}
