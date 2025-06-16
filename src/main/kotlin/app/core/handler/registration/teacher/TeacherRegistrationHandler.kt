package app.core.handler.registration.teacher

import app.core.handler.registration.RoleRegistrationHandler
import app.core.model.person.RegistrationRequest
import app.core.model.person.Teacher
import app.core.service.teacher.ITeacherService
import app.web.model.Login
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class TeacherRegistrationHandler(
    private val teacherService: ITeacherService
) : RoleRegistrationHandler {
    override fun supports(role: String) = role == "TEACHER"

    override fun register(request: RegistrationRequest) {
        teacherService.createActualTeacher(
            Login(request.email, request.passwordHash),
            Teacher(
                personId = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                surname = request.surname,
                name = request.name,
                middleName = request.middleName,
                email = request.email
            )
        )
    }
}
