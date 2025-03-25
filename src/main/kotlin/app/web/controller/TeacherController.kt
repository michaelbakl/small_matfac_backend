package app.web.controller

import app.core.converter.LocalDateConverter
import app.core.converter.TeacherConverter
import app.core.converter.TeacherUpdateConverter
import app.core.exception.NotFoundException
import app.core.service.login.LoginService
import app.core.service.teacher.ITeacherService
import app.core.util.CommonResponse
import app.core.validation.IdValidator
import app.web.model.Login
import app.web.model.request.teacher.AddTeacherRequest
import app.web.model.request.teacher.UpdateTeacherInfoRequest
import app.web.model.response.person.GetTeacherInfoResponse
import jakarta.validation.Valid
import jakarta.websocket.server.PathParam
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import ru.baklykov.app.core.model.person.Teacher
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import java.util.*

@RestController
@RequestMapping("/teachers")
class TeacherController(private val service: ITeacherService, private val loginService: LoginService) {

    @PostMapping(path=["/create"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun createTeacher(
        @Valid @RequestBody request: AddTeacherRequest,
        bindingResult: BindingResult
    ): ResponseEntity<CommonResponse<GetTeacherInfoResponse>> {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity
                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(CommonResponse(true, bindingResult.toString(), ""))
            }
            request.teacherId = UUID.randomUUID()
            val response: GetTeacherInfoResponse =
                service.createActualTeacher(Login(request.email, request.password),TeacherConverter.convertToModel(request))

            val commonResponse: CommonResponse<GetTeacherInfoResponse> = CommonResponse(response = response)

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

    @PutMapping(path=["/teacher"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun updateTeacher(
        @Valid @RequestBody request: UpdateTeacherInfoRequest,
        bindingResult: BindingResult
    ): ResponseEntity<CommonResponse<GetTeacherInfoResponse>> {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity
                    .ok()
                    .body(CommonResponse(true, bindingResult.toString(), ""))
            }

            val newTeacher: Teacher = TeacherUpdateConverter.convert(request)
            val response: GetTeacherInfoResponse =
                service.updateActualTeacher(newTeacher, LocalDateConverter.convert(request.dateOfChanging) ?: LocalDateTime.now())

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

    @GetMapping( path = ["/{teacherId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getTeacherById(
        @Valid @PathParam("teacherId") teacherId: String
    ): ResponseEntity<CommonResponse<GetTeacherInfoResponse>> {
        try {
            if (!IdValidator.validate(teacherId)) {
                return ResponseEntity
                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(CommonResponse(true, "Id is not UUID", ""))
            }
            val response: GetTeacherInfoResponse = service.getActualTeacher(UUID.fromString(teacherId))
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

    @GetMapping( path = ["/get_by_user_id/{userId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getTeacherByUserId(
        @Valid @PathParam("userId") userId: String
    ): ResponseEntity<CommonResponse<GetTeacherInfoResponse>> {
        try {
            if (!IdValidator.validate(userId)) {
                return ResponseEntity
                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(CommonResponse(true, "Id is not UUID", ""))
            }
            val response: GetTeacherInfoResponse = service.getTeacherInfoByUserId(UUID.fromString(userId))
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

    @DeleteMapping( path = ["/{teacherId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun removeTeacher(
        @PathParam("teacherId") teacherId: String
    ): ResponseEntity<CommonResponse<Int>> {
        return try {
            if (!IdValidator.validate(teacherId)) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse(true, "Id is not UUID", ""))
            }
            val response: Int = service.deleteActualTeacher(UUID.fromString(teacherId))
            ResponseEntity
                .ok()
                .body(CommonResponse(response = response))
        } catch (e: NotFoundException) {
            ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CommonResponse(true, e.toString(), ""))
        }
    }

    @GetMapping( path = ["/get_all_actual_teachers"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getAllActualTeachers(): ResponseEntity<CommonResponse<List<GetTeacherInfoResponse>>> {
        return try {
            val response: List<GetTeacherInfoResponse> = service.getAllActualTeachers()
            ResponseEntity
                .ok()
                .body(CommonResponse(response = response))
        } catch (e: NotFoundException) {
            ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CommonResponse(true, e.toString(), ""))
        } catch (e: Exception) {
            ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse(true, e.toString(), ""))
        }
    }

}