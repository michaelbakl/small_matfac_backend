package app.web.controller

import ru.baklykov.app.core.converter.teacher.TeacherConverter
import ru.baklykov.app.core.converter.teacher.TeacherUpdateConverter
import app.core.exception.NotFoundException
import app.core.filter.TeacherFilter
import app.core.service.login.LoginService
import app.core.service.teacher.ITeacherService
import app.core.util.CommonResponse
import app.core.validation.IdValidator
import app.web.model.Login
import app.web.model.request.teacher.AddTeacherRequest
import app.web.model.request.teacher.UpdateTeacherInfoRequest
import app.web.model.response.person.GetTeacherInfoResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import ru.baklykov.app.core.converter.datetime.ZonedDateConverter
import ru.baklykov.app.core.converter.util.IdConverter
import app.core.model.person.Teacher
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException
import java.util.*

@Tag(name = "Teacher API", description = "API для управления преподавателями")
@RestController
@RequestMapping("/api/teachers")
class TeacherController(private val service: ITeacherService, private val loginService: LoginService) {

    @Operation(
        summary = "Создание преподавателя",
        description = "Создает нового преподавателя с привязкой к пользователю",
        responses = [
            ApiResponse(responseCode = "200", description = "Преподаватель создан"),
            ApiResponse(responseCode = "415", description = "Ошибка валидации или неподдерживаемый формат запроса"),
            ApiResponse(responseCode = "400", description = "Ошибка при обработке запроса")
        ]
    )
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
                service.createActualTeacher(Login(request.email, request.password), TeacherConverter.convertToModel(request))

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

    @Operation(
        summary = "Обновление преподавателя",
        description = "Обновляет данные существующего преподавателя",
        responses = [
            ApiResponse(responseCode = "200", description = "Преподаватель обновлён"),
            ApiResponse(responseCode = "415", description = "Ошибка даты или неподдерживаемый формат"),
            ApiResponse(responseCode = "404", description = "Преподаватель не найден")
        ]
    )
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
                service.updateActualTeacher(newTeacher, ZonedDateConverter.convert(request.dateOfChanging) ?: ZonedDateTime.now())

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

    @Operation(
        summary = "Получить преподавателя по ID",
        description = "Возвращает данные преподавателя по его UUID",
        responses = [
            ApiResponse(responseCode = "200", description = "Данные преподавателя получены"),
            ApiResponse(responseCode = "415", description = "ID не является UUID"),
            ApiResponse(responseCode = "404", description = "Преподаватель не найден")
        ]
    )
    @GetMapping( path = ["/{teacherId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getTeacherById(
        @Valid @PathVariable("teacherId") teacherId: String
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

    @Operation(
        summary = "Получить преподавателя по userId",
        description = "Возвращает данные преподавателя по UUID пользователя",
        responses = [
            ApiResponse(responseCode = "200", description = "Данные преподавателя получены"),
            ApiResponse(responseCode = "415", description = "ID не является UUID"),
            ApiResponse(responseCode = "404", description = "Преподаватель не найден")
        ]
    )
    @GetMapping( path = ["/get_by_user_id/{userId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getTeacherByUserId(
        @Valid @PathVariable("userId") userId: String
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

    @Operation(
        summary = "Удалить преподавателя",
        description = "Удаляет преподавателя по ID",
        responses = [
            ApiResponse(responseCode = "200", description = "Преподаватель удалён"),
            ApiResponse(responseCode = "400", description = "Невалидный UUID"),
            ApiResponse(responseCode = "404", description = "Преподаватель не найден")
        ]
    )
    @DeleteMapping( path = ["/{teacherId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun removeTeacher(
        @PathVariable("teacherId") teacherId: String
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

    @Operation(
        summary = "Получить всех преподавателей",
        description = "Возвращает список всех актуальных преподавателей",
        responses = [
            ApiResponse(responseCode = "200", description = "Список получен"),
            ApiResponse(responseCode = "404", description = "Преподаватели не найдены")
        ]
    )
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

    @Operation(
        summary = "Фильтрация преподавателей по параметрам",
        description = "Позволяет фильтровать преподавателей по ID, userId, ФИО, email и дате рождения",
        responses = [
            ApiResponse(responseCode = "200", description = "Список преподавателей"),
            ApiResponse(responseCode = "404", description = "Нет совпадений"),
            ApiResponse(responseCode = "400", description = "Ошибка запроса")
        ]
    )
    @GetMapping( path = ["/get_with_params"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getTeachersWithParams(
        @Valid @RequestParam("teacherId", required = false) teacherId: String? = null,
        @Valid @RequestParam("userId", required = false) userId: String? = null,
        @Valid @RequestParam("surname", required = false) surname: String? = null,
        @Valid @RequestParam("name", required = false) name: String? = null,
        @Valid @RequestParam("middlename", required = false) middleName: String? = null,
        @Valid @RequestParam("email", required = false) email: String? = null,
        @Valid @RequestParam("dateofbirthl", required = false) firstDate: String? = null,
        @Valid @RequestParam("dateofbirthr", required = false) secondDate: String? = null,

    ): ResponseEntity<CommonResponse<List<GetTeacherInfoResponse>>> {
        return try {
            val filter: TeacherFilter = TeacherFilter(
                IdConverter.convertTo(teacherId),
                IdConverter.convertTo(userId),
                surname,
                name,
                middleName,
                email,
                ZonedDateConverter.convert(firstDate),
                ZonedDateConverter.convert(secondDate)
            )
            val response: List<GetTeacherInfoResponse> = service.getTeachersByFilter(filter)
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
