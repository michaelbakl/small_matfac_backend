package app.web.controller

import app.core.exception.NotFoundException
import app.core.util.CommonResponse
import app.core.validation.IdValidator
import jakarta.validation.Valid
import jakarta.websocket.server.PathParam
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import ru.baklykov.app.core.converter.GameConfigConverter
import ru.baklykov.app.core.converter.room.RoomConverter
import ru.baklykov.app.core.converter.ZonedDateConverter
import ru.baklykov.app.core.converter.room.UpdateRoomConverter
import ru.baklykov.app.core.filter.RoomFilter
import ru.baklykov.app.core.service.game.IGameService
import ru.baklykov.app.core.service.room.IRoomService
import ru.baklykov.app.web.model.request.game.CreateGameRequest
import ru.baklykov.app.web.model.request.game.UpdateDatesInGameRequest
import ru.baklykov.app.web.model.request.room.AddParticipantsToRoomRequest
import ru.baklykov.app.web.model.request.room.AddRoomRequest
import ru.baklykov.app.web.model.request.room.UpdateParticipantsInRoomRequest
import ru.baklykov.app.web.model.request.room.UpdateRoomRequest
import ru.baklykov.app.web.model.response.game.GetGameInfoResponse
import ru.baklykov.app.web.model.response.room.GetRoomInfoResponse
import java.time.format.DateTimeParseException

@RestController
@RequestMapping("/rooms")
class RoomController(private val roomService: IRoomService, private val gameService: IGameService) {

    @PostMapping(
        path = ["/create"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
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

    @PostMapping(
        path = ["/add_participants"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun addParticipantsToGroup(
        @Valid @RequestBody request: AddParticipantsToRoomRequest,
        bindingResult: BindingResult
    ): ResponseEntity<CommonResponse<GetRoomInfoResponse>> {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity
                    .ok()
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

    @PutMapping(
        path = ["/update_participants"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
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

    @GetMapping(path = ["/{roomId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getRoomById(
        @Valid @PathParam("roomId") roomId: String
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

    @GetMapping(path = ["/get_rooms_with_params"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getRoomsWithParams(
        @Valid @RequestParam("roomId") roomId: String?,
        @Valid @RequestParam("teacherId") teacherId: String?,
        @Valid @RequestParam("name") name: String?,
        @Valid @RequestParam("openingDate") firstDate: String?,
        @Valid @RequestParam("closingDate") secondDate: String?,
    ): ResponseEntity<CommonResponse<List<GetRoomInfoResponse>>> {
        return try {
            val filter = RoomFilter(roomId, teacherId, name, ZonedDateConverter.convert(firstDate), ZonedDateConverter.convert(secondDate))
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


    @PutMapping(
        path = ["/{roomId}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun updateRoom(
        @PathParam("roomId") roomId: String,
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

    @DeleteMapping(path = ["/remove_room"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
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

    @PostMapping(path = ["/{roomId}/games/{gameId}/start"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
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

    @PostMapping(path = ["/{roomId}/games/create"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun createGameInRoom(
        @PathParam("roomId") roomId: String,
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

            val response = gameService.createGame(roomId, request.name, GameConfigConverter.convert(request), request.categories)

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
