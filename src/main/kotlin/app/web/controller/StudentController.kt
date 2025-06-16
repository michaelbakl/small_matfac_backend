package app.web.controller

import ru.baklykov.app.core.converter.StudentConverter
import app.core.exception.NotFoundException
import app.core.filter.StudentFilter
import app.core.service.student.IStudentService
import app.core.util.CommonResponse
import app.core.validation.IdValidator
import app.web.model.Login
import app.web.model.request.student.AddStudentRequest
import app.web.model.response.person.GetStudentInfoResponse
import app.web.model.response.student.GetStudentsResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import ru.baklykov.app.core.converter.datetime.ZonedDateConverter
import ru.baklykov.app.core.converter.util.IdConverter
import java.time.format.DateTimeParseException
import java.util.*

@Tag(name = "Student API", description = "Operations related to student management")
@RestController
@RequestMapping("/api/students")
class StudentController(private val service: IStudentService) {


    @Operation(
        summary = "Create a new student",
        description = "Adds a new student to the system",
        responses = [
            ApiResponse(responseCode = "200", description = "Student created"),
            ApiResponse(responseCode = "400", description = "Invalid input"),
            ApiResponse(responseCode = "415", description = "Validation errors")
        ]
    )
    @PostMapping(path=["/create"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun createStudent(
        @Valid @RequestBody request: AddStudentRequest,
        bindingResult: BindingResult
    ): ResponseEntity<CommonResponse<GetStudentInfoResponse>> {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity
                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(CommonResponse(true, bindingResult.toString(), ""))
            }
            val response: GetStudentInfoResponse =
                service.addActualStudent(Login(request.email, request.password), StudentConverter.convertToModel(request))

            val commonResponse: CommonResponse<GetStudentInfoResponse> = CommonResponse(response = response)

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
        summary = "Get student by ID",
        description = "Retrieve student details using their UUID",
        responses = [
            ApiResponse(responseCode = "200", description = "Student found"),
            ApiResponse(responseCode = "404", description = "Student not found"),
            ApiResponse(responseCode = "415", description = "Invalid UUID format")
        ]
    )
    @GetMapping(path = ["/student/{studentId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getStudentById(
        @Valid @PathVariable("studentId") studentId: String
    ): ResponseEntity<CommonResponse<GetStudentInfoResponse>> {
        try {
            if (!IdValidator.validate(studentId)) {
                return ResponseEntity
                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(CommonResponse(true, "Id is not UUID", ""))
            }
            val response = service.getActualStudent(UUID.fromString(studentId))
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
        summary = "Get students by groups",
        description = "Fetch all students belonging to specified groups",
        responses = [
            ApiResponse(responseCode = "200", description = "Students retrieved"),
            ApiResponse(responseCode = "400", description = "Invalid group ID format")
        ]
    )
    @GetMapping(path = ["/get_students_by_groups"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getStudentsByGroups(
        @Valid @RequestParam("groups") groups: List<String>?
    ): ResponseEntity<CommonResponse<GetStudentsResponse>> {
        try {
            val groupsIds = mutableListOf<UUID>()
            groups?.map { group ->
                if (!IdValidator.validate(group)) {
                    return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(CommonResponse(true, "Wrong inputs", ""))
                }
                groupsIds.add(UUID.fromString(group))
            }
            val response: GetStudentsResponse = service.getStudentByGroups(groupsIds)
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
        summary = "Remove a student",
        description = "Deletes a student by their ID",
        responses = [
            ApiResponse(responseCode = "200", description = "Student removed"),
            ApiResponse(responseCode = "404", description = "Student not found"),
            ApiResponse(responseCode = "400", description = "Invalid UUID")
        ]
    )
    @DeleteMapping(path = ["/remove_student"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun removeStudent(
        @RequestParam("studentId") studentId: String
    ): ResponseEntity<CommonResponse<Int>> {
        try {
            if (!IdValidator.validate(studentId)) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse(true, "Id is not UUID", ""))
            }

            val response: Int = service.deleteActualStudent(UUID.fromString(studentId))
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
        summary = "Search students by parameters",
        description = "Retrieve students filtered by multiple optional criteria",
        responses = [
            ApiResponse(responseCode = "200", description = "Students retrieved"),
            ApiResponse(responseCode = "404", description = "No matching students found"),
            ApiResponse(responseCode = "400", description = "Invalid request")
        ]
    )
    @GetMapping( path = ["/get_with_params"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getStudentsWithParams(
        @Valid @RequestParam("studentId", required = false) teacherId: String? = null,
        @Valid @RequestParam("userId", required = false) userId: String? = null,
        @Valid @RequestParam("surname", required = false) surname: String? = null,
        @Valid @RequestParam("name", required = false) name: String? = null,
        @Valid @RequestParam("middlename", required = false) middleName: String? = null,
        @Valid @RequestParam("email", required = false) email: String? = null,
        @Valid @RequestParam("dateofbirthl", required = false) firstDateBirth: String? = null,
        @Valid @RequestParam("dateofbirthr", required = false) secondDateBirth: String? = null,
        @Valid @RequestParam("dateofenteringl", required = false) firstDateEntering: String? = null,
        @Valid @RequestParam("dateofenteringr", required = false) secondDateEntering: String? = null,
        @Valid @RequestParam("groups", required = false) groups: List<String>? = null,
        @Valid @RequestParam("rooms", required = false) rooms: List<String>? = null,
        @Valid @RequestParam("games", required = false) games: List<String>? = null,

        ): ResponseEntity<CommonResponse<List<GetStudentInfoResponse>>> {
        return try {
            val filter: StudentFilter = StudentFilter(
                IdConverter.convertTo(teacherId),
                IdConverter.convertTo(userId),
                surname,
                name,
                middleName,
                email,
                ZonedDateConverter.convert(firstDateBirth),
                ZonedDateConverter.convert(secondDateBirth),
                ZonedDateConverter.convert(firstDateEntering),
                ZonedDateConverter.convert(secondDateEntering),
                IdConverter.convertToSecond(groups),
                IdConverter.convertToSecond(rooms),
                IdConverter.convertToSecond(games),
            )
            val response: List<GetStudentInfoResponse> = service.getStudentsByFilter(filter)
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
