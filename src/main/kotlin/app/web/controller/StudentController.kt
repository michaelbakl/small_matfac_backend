package app.web.controller

import app.core.converter.StudentConverter
import app.core.exception.NotFoundException
import app.core.service.student.IStudentService
import app.core.util.CommonResponse
import app.core.validation.IdValidator
import app.web.model.Login
import app.web.model.request.student.AddStudentRequest
import app.web.model.response.person.GetStudentInfoResponse
import app.web.model.response.student.GetStudentsResponse
import jakarta.validation.Valid
import jakarta.websocket.server.PathParam
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.time.format.DateTimeParseException
import java.util.*

@RestController
@RequestMapping("/students")
class StudentController(private val service: IStudentService) {


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
            request.studentId = UUID.randomUUID()
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

    @GetMapping(path = ["/student/{studentId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getStudentById(
        @Valid @PathParam("studentId") studentId: String
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

}
