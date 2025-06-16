package app.web.controller

import app.core.exception.NotFoundException
import app.core.util.CommonResponse
import app.core.validation.IdValidator
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import app.core.converter.game.GameConfigConverter
import ru.baklykov.app.core.converter.room.RoomConverter
import ru.baklykov.app.core.converter.datetime.ZonedDateConverter
import ru.baklykov.app.core.converter.room.UpdateRoomConverter
import ru.baklykov.app.core.filter.RoomFilter
import app.core.service.game.IGameService
import ru.baklykov.app.core.service.room.IRoomService
import app.web.model.request.game.CreateGameRequest
import app.web.model.request.game.UpdateDatesInGameRequest
import app.web.model.request.room.AddParticipantsToRoomRequest
import app.web.model.request.room.AddRoomRequest
import app.web.model.request.room.UpdateParticipantsInRoomRequest
import app.web.model.request.room.UpdateRoomRequest
import app.web.model.response.game.GetGameInfoResponse
import app.web.security.AuthRolesRequired
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import app.web.model.response.room.GetRoomInfoResponse
import java.time.format.DateTimeParseException
import java.util.UUID

@Tag(name = "Room Management", description = "Операции управления комнатами и играми")
@RestController
@RequestMapping("api/rooms")
class RoomController(private val roomService: IRoomService, private val gameService: IGameService) {

    @Operation(summary = "Получить все комнаты", description = "Возвращает список всех доступных комнат")
    @ApiResponse(responseCode = "200", description = "Успешный ответ")
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getAllRooms(): ResponseEntity<CommonResponse<List<GetRoomInfoResponse>>> {
        try {
//            if (bindingResult.hasErrors()) {
//                return ResponseEntity
//                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
//                    .body(CommonResponse(true, bindingResult.toString(), ""))
//            }


            val response: List<GetRoomInfoResponse> =
                roomService.getRoomsWithParams(RoomFilter())

            val commonResponse: CommonResponse<List<GetRoomInfoResponse>> = CommonResponse(response = response)

            return ResponseEntity
                .ok()
                .body(commonResponse)
        } catch (e: DateTimeParseException) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse(true, e.toString(), ""))

        } catch (e: Exception) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse(true, e.toString(), ""))
        }
    }


    @Operation(summary = "Создать комнату", description = "Создаёт новую комнату")
    @ApiResponse(responseCode = "200", description = "Комната создана")
    @ApiResponse(responseCode = "415", description = "Ошибки валидации данных")
    @PostMapping(
        path = ["/create"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    @AuthRolesRequired("ADMIN", "MODERATOR", "TEACHER")
    fun createRoom(
        @Valid @RequestBody request: AddRoomRequest,
        bindingResult: BindingResult
    ): ResponseEntity<CommonResponse<GetRoomInfoResponse>> {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity
                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(CommonResponse(true, bindingResult.toString(), ""))
            }


            val response: GetRoomInfoResponse =
                roomService.addRoom(RoomConverter.convertToModel(request))

            val commonResponse: CommonResponse<GetRoomInfoResponse> = CommonResponse(response = response)

            return ResponseEntity
                .ok()
                .body(commonResponse)
        } catch (e: DateTimeParseException) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse(true, e.toString(), ""))

        } catch (e: Exception) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse(true, e.toString(), ""))
        }
    }

    @Operation(summary = "Добавить участников в комнату", description = "Добавляет пользователей в указанную комнату")
    @ApiResponse(responseCode = "200", description = "Участники успешно добавлены")
    @ApiResponse(responseCode = "404", description = "Комната не найдена")
    @PostMapping(
        path = ["/add_participants"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    @AuthRolesRequired("ADMIN", "MODERATOR", "TEACHER")
    fun addParticipantsToGroup(
        @Valid @RequestBody request: AddParticipantsToRoomRequest,
        bindingResult: BindingResult
    ): ResponseEntity<CommonResponse<GetRoomInfoResponse>> {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity
                    .badRequest()
                    .body(CommonResponse(true, bindingResult.toString(), ""))
            }

            // TODO: add credentials check
//            userCredentials.userId?.let {
//                if (
//                    credentialsService.findTeacherIdByUserId(UUID.fromString(it)) !=
//                    credentialsService.findTeacherIdByLessonId(UUID.fromString(request.lessonId))
//                ) {
//                    return ResponseEntity
//                        .ok()
//                        .body(CommonResponse(true, "User is not an owner of the lesson", ""))
//                }
//            } ?: return ResponseEntity
//                .ok()
//                .body(CommonResponse(true, "Unable to determine user", ""))

            val response: GetRoomInfoResponse =
                roomService.addPlayersToRoom(request.roomId, request.participants)

            return ResponseEntity
                .ok()
                .body(CommonResponse(response = response))
        } catch (e: DateTimeParseException) {
            return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(CommonResponse(true, e.toString(), ""))
        } catch (e: NotFoundException) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CommonResponse(true, e.toString(), ""))
        } catch (e: Exception) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse(true, e.toString(), ""))
        }
    }

    @Operation(summary = "Обновить участников в комнате", description = "Полностью заменяет список участников комнаты")
    @ApiResponse(responseCode = "200", description = "Список участников обновлён")
    @ApiResponse(responseCode = "404", description = "Комната не найдена")
    @PutMapping(
        path = ["/update_participants"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    @AuthRolesRequired("ADMIN", "MODERATOR", "TEACHER")
    fun updateParticipantsInRoom(
        @Valid @RequestBody request: UpdateParticipantsInRoomRequest,
        bindingResult: BindingResult
    ): ResponseEntity<CommonResponse<GetRoomInfoResponse>> {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity
                    .ok()
                    .body(CommonResponse(true, bindingResult.toString(), ""))
            }

//            userCredentials.userId?.let {
//                if (
//                    credentialsService.findTeacherIdByUserId(UUID.fromString(it)) !=
//                    credentialsService.findTeacherIdByLessonId(UUID.fromString(request.lessonId))
//                ) {
//                    return ResponseEntity
//                        .status(HttpStatus.FORBIDDEN)
//                        .body(CommonResponse(true, "User is not an owner of the lesson", ""))
//                }
//            } ?: return ResponseEntity
//                .status(HttpStatus.FORBIDDEN)
//                .body(CommonResponse(true, "Unable to determine user", ""))

            val response: GetRoomInfoResponse =
                roomService.updatePlayersInRoom(request.roomId, request.participants)

            return ResponseEntity
                .ok()
                .body(CommonResponse(response = response))
        } catch (e: DateTimeParseException) {
            return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(CommonResponse(true, e.toString(), ""))
        } catch (e: NotFoundException) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CommonResponse(true, e.toString(), ""))
        } catch (e: Exception) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse(true, e.toString(), ""))
        }
    }

    @Operation(summary = "Получить информацию о комнате", description = "Возвращает данные о комнате по её ID")
    @ApiResponse(responseCode = "200", description = "Информация о комнате найдена")
    @ApiResponse(responseCode = "404", description = "Комната не найдена")
    @GetMapping(path = ["/{roomId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    @AuthRolesRequired("ADMIN", "MODERATOR", "TEACHER", "STUDENT")
    fun getRoomById(
        @Valid @PathVariable("roomId") roomId: String
    ): ResponseEntity<CommonResponse<GetRoomInfoResponse>> {
        try {
            if (!IdValidator.validate(roomId)) {
                return ResponseEntity
                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(CommonResponse(true, "Id is not UUID", ""))
            }
            val response: GetRoomInfoResponse = roomService.getRoomById(roomId)
            return ResponseEntity
                .ok()
                .body(CommonResponse(response = response))
        } catch (e: NotFoundException) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CommonResponse(true, e.toString(), ""))
        } catch (e: Exception) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse(true, e.toString(), ""))
        }
    }

    @Operation(
        summary = "Получить комнаты по параметрам",
        description = "Фильтрация комнат по ID, учителю, имени, студентам, дате и статусу"
    )
    @ApiResponse(responseCode = "200", description = "Комнаты найдены")
    @GetMapping(path = ["/get_rooms_with_params"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    @AuthRolesRequired("ADMIN", "MODERATOR", "TEACHER", "STUDENT")
    fun getRoomsWithParams(
        @Valid @RequestParam("roomId", required = false) roomId: String? = null,
        @Valid @RequestParam("teacherId", required = false) teacherId: String? = null,
        @Valid @RequestParam("name", required = false) name: String? = null,
        @Valid @RequestParam("studentIds", required = false) students: List<String>? = listOf(),
        @Valid @RequestParam("isClosed", required = false) isClosed: Boolean? = null,
        @Valid @RequestParam("firstDate", required = false) firstDate: String? = null,
        @Valid @RequestParam("secondDate", required = false) secondDate: String? = null,
    ): ResponseEntity<CommonResponse<List<GetRoomInfoResponse>>> {
        return try {
            val filter = RoomFilter(
                roomId,
                teacherId,
                name,
                students,
                isClosed,
                ZonedDateConverter.convert(firstDate),
                ZonedDateConverter.convert(secondDate)
            )
            val response: List<GetRoomInfoResponse> = roomService.getRoomsWithParams(filter)
            ResponseEntity
                .ok()
                .body(CommonResponse(response = response))
        } catch (e: NotFoundException) {
            ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CommonResponse(true, e.toString(), ""))
        } catch (e: Exception) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse(true, e.toString(), ""))
        }
    }


    @Operation(summary = "Обновить данные комнаты", description = "Обновляет информацию о комнате")
    @ApiResponse(responseCode = "200", description = "Комната обновлена")
    @ApiResponse(responseCode = "400", description = "Неверный ID")
    @ApiResponse(responseCode = "404", description = "Комната не найдена")
    @PutMapping(
        path = ["/{roomId}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    @AuthRolesRequired("ADMIN", "MODERATOR", "TEACHER")
    fun updateRoom(
        @PathVariable("roomId") roomId: String,
        @Valid @RequestBody request: UpdateRoomRequest,
        bindingResult: BindingResult
    ): ResponseEntity<CommonResponse<GetRoomInfoResponse>> {
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(CommonResponse(true, bindingResult.toString(), ""))
        }
        return try {
            if (!IdValidator.validate(roomId)) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse(true, "Id is not UUID", ""))
            }

//            userCredentials.userId?.let {
//                if (
//                    credentialsService.findTeacherIdByUserId(UUID.fromString(it)) !=
//                    credentialsService.findTeacherIdByLessonId(UUID.fromString(lessonId))
//                ) {
//                    return ResponseEntity
//                        .ok()
//                        .body(CommonResponse(true, "User is not an owner of the lesson", ""))
//                }
//            } ?: return ResponseEntity
//                .ok()
//                .body(CommonResponse(true, "Unable to determine user", ""))

            val response: GetRoomInfoResponse = roomService.updateRoom(UpdateRoomConverter.convertToModel(request))
            ResponseEntity
                .ok()
                .body(CommonResponse(response = response))
        } catch (e: NotFoundException) {
            ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CommonResponse(true, e.toString(), ""))
        } catch (e: Exception) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse(true, e.toString(), ""))
        }
    }

    @Operation(summary = "Удалить комнату", description = "Удаляет комнату по ID")
    @ApiResponse(responseCode = "200", description = "Комната удалена")
    @ApiResponse(responseCode = "404", description = "Комната не найдена")
    @DeleteMapping(path = ["/remove_room"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    @AuthRolesRequired("ADMIN", "MODERATOR", "TEACHER")
    fun removeRoom(
        @RequestParam("roomId") roomId: String
    ): ResponseEntity<CommonResponse<Int>> {
        try {
            if (!IdValidator.validate(roomId)) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse(true, "Id is not UUID", ""))
            }

//            userCredentials.userId?.let {
//                if (
//                    credentialsService.findTeacherIdByUserId(UUID.fromString(it)) !=
//                    credentialsService.findTeacherIdByLessonId(UUID.fromString(lessonId))
//                ) {
//                    return ResponseEntity
//                        .status(HttpStatus.FORBIDDEN)
//                        .body(CommonResponse(true, "User is not an owner of the lesson", ""))
//                }
//            } ?: return ResponseEntity
//                .status(HttpStatus.FORBIDDEN)
//                .body(CommonResponse(true, "Unable to determine user", ""))

            val response: Int = roomService.removeRoom(roomId)
            return ResponseEntity
                .ok()
                .body(CommonResponse(response = response))
        } catch (e: NotFoundException) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CommonResponse(true, e.toString(), ""))
        } catch (e: Exception) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse(true, e.toString(), ""))
        }
    }

    @Operation(
        summary = "Запустить игру в комнате",
        description = "Запускает или обновляет игру в комнате по времени"
    )
    @ApiResponse(responseCode = "200", description = "Игра запущена или обновлена")
    @ApiResponse(responseCode = "400", description = "Неверный формат ID или игра не найдена в комнате")
    @PostMapping(path = ["/{roomId}/games/{gameId}/start"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    @AuthRolesRequired("ADMIN", "MODERATOR", "TEACHER")
    fun startGameByTeacherByTime(
        @RequestParam("roomId") roomId: String,
        @RequestParam("gameId") gameId: String,
        @RequestBody request: UpdateDatesInGameRequest
    ): ResponseEntity<CommonResponse<GetGameInfoResponse>> {
        try {
            if (!IdValidator.validate(roomId) || !IdValidator.validate(gameId)) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse(true, "Id is not UUID", ""))
            }
            if (!roomService.checkGameExists(roomId, gameId)) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse(true, "Game does not exist in the room", ""))
            }
            val startDate = ZonedDateConverter.convert(request.startDate)
            val finishDate = ZonedDateConverter.convert(request.finishDate)

            val response = if (!gameService.checkGameStarted(roomId, gameId)) {
                gameService.startGameInRoom(roomId, gameId)
            } else {
                if (startDate != null && finishDate != null) {
                    gameService.changeGameDates(roomId, gameId, startDate, finishDate)
                }
                gameService.getGameById(roomId, gameId)
            }

            return ResponseEntity
                .ok()
                .body(CommonResponse(response = response))
        } catch (e: NotFoundException) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CommonResponse(true, e.toString(), ""))
        } catch (e: Exception) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse(true, e.toString(), ""))
        }
    }

    @Operation(
        summary = "Создать игру в комнате",
        description = "Создаёт новую игру в указанной комнате"
    )
    @ApiResponse(responseCode = "200", description = "Игра успешно создана")
    @ApiResponse(responseCode = "415", description = "Ошибки валидации данных")
    @PostMapping(path = ["/{roomId}/games/create"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    @AuthRolesRequired("ADMIN", "MODERATOR", "TEACHER")
    fun createGameInRoom(
        @PathVariable("roomId") roomId: String,
        @Valid @RequestBody request: CreateGameRequest,
        bindingResult: BindingResult
    ): ResponseEntity<CommonResponse<GetGameInfoResponse>> {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity
                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(CommonResponse(true, bindingResult.toString(), ""))
            }
            if (!IdValidator.validate(roomId)) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse(true, "Id is not UUID", ""))
            }

            // TODO add creator id from credentials

            val response = gameService.createGame(
                creatorId = UUID.randomUUID().toString(),
                roomId,
                request.name,
                GameConfigConverter.convert(request),
                request.categories.toString()
            )

            return ResponseEntity
                .ok()
                .body(CommonResponse(response = response))
        } catch (e: NotFoundException) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CommonResponse(true, e.toString(), ""))
        } catch (e: Exception) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse(true, e.toString(), ""))
        }
    }

}
