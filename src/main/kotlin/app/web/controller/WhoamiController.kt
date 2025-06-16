package app.web.controller

import app.core.service.teacher.ITeacherService
import app.core.util.CommonResponse
import app.web.model.response.person.GetTeacherInfoResponse
import app.web.security.AuthRolesRequired
import app.web.security.UserCredentials
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.util.UUID


@RestController
@RequestMapping("api/whoami")
class WhoamiController(private val teacherService: ITeacherService) {

    /**
     * method for getting whoami info
     * @param userCredentials - user info
     * @return response body for who am i
     */
    @GetMapping
    @ResponseBody
    @AuthRolesRequired("TEACHER")
    fun getTeacher(
        userCredentials: UserCredentials
    ): ResponseEntity<CommonResponse<GetTeacherInfoResponse>> {
        try {
            val teacherInfo = teacherService.getTeacherInfoByUserId(UUID.fromString(userCredentials.userId))
            return ResponseEntity
                .ok()
                .body(CommonResponse(response = teacherInfo))
        } catch (e: Exception) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse(true, e.toString(), ""))
        }
    }


}