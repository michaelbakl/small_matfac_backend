package app.web.controller

import app.core.exception.NotFoundException
import app.core.service.subject.ISubjectService
import app.core.validation.IdValidator
import app.core.util.CommonResponse
import app.web.model.request.subject.AddSubjectRequest
import app.web.model.request.subject.DeleteSubjectRequest
import app.web.model.request.subject.GetSubjectsWithParamsRequest
import app.web.model.request.subject.UpdateSubjectRequest
import app.web.model.response.subject.GetSubjectResponse
import app.web.model.response.subject.GetSubjectsResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import ru.baklykov.app.core.model.Subject
import java.time.format.DateTimeParseException
import java.util.*


@RestController
@RequestMapping("/subjects")
class SubjectController(private val service: ISubjectService) {

    @PostMapping(path = ["/add_subject"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun addSubject(
        @Valid @RequestBody request: AddSubjectRequest,
        bindingResult: BindingResult
    ): ResponseEntity<CommonResponse<GetSubjectResponse>> {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse(true, bindingResult.toString(), ""))
            }
            val response: GetSubjectResponse =
                service.addSubject(
                    Subject(
                        UUID.randomUUID(),
                        request.name
                    )
                )
            return ResponseEntity
                .ok()
                .body(CommonResponse(response = response))
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

    @GetMapping(path = ["/get_subject/{subjectId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getSubjectById(@PathVariable("subjectId") id: String):
            ResponseEntity<CommonResponse<GetSubjectResponse>> {
        try {
            if (!IdValidator.validate(id)) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse(true, "Id is not UUID", ""))
            }
            val response: GetSubjectResponse = service.getSubjectById(id)
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

    @PostMapping(path = ["/update_subject"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun updateSubject(
        @Valid @RequestBody request: UpdateSubjectRequest,
        bindingResult: BindingResult
    ): ResponseEntity<CommonResponse<GetSubjectResponse>> {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse(true, bindingResult.toString(), ""))
            }
            val subject = Subject(
                UUID.fromString(request.id),
                request.name
            )
            val response: GetSubjectResponse = service.updateSubject(subject)
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

    @DeleteMapping(path = ["/remove_subject"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun deleteSubject(
        @Valid @RequestBody request: DeleteSubjectRequest,
        bindingResult: BindingResult
    ): ResponseEntity<CommonResponse<GetSubjectResponse>> {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse(true, bindingResult.toString(), ""))
            }
            val response: GetSubjectResponse = service.deleteSubjectById(request.id)
            return ResponseEntity
                .ok()
                .body(CommonResponse(response = response))
        } catch (e: NotFoundException) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(CommonResponse(true, e.toString(), ""))
        } catch (e: Exception) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(CommonResponse(true, e.toString(), ""))
        }
    }

    @GetMapping(path = ["/get_subjects_with_params"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getSubjectsByFilter(
        @RequestParam("subjectId") id: String?,
        @RequestParam("name") name: String?
    ): ResponseEntity<CommonResponse<GetSubjectsResponse>> {
        return try {
            val response: GetSubjectsResponse =
                service.getSubjectsByFilter(GetSubjectsWithParamsRequest(id, name))
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

    @GetMapping(path = ["/get_all_subjects"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getAllSubjects(): ResponseEntity<CommonResponse<GetSubjectsResponse>> {
        return try {
            val response: GetSubjectsResponse = service.getAll()

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
