package app.core.handler.registration.student

import app.core.handler.registration.RoleRegistrationHandler
import app.core.model.person.RegistrationRequest
import app.core.model.person.Student
import app.core.service.student.IStudentService
import app.web.model.Login
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class StudentRegistrationHandler(
    private val studentService: IStudentService
) : RoleRegistrationHandler {

    override fun supports(role: String) = role == "STUDENT"

    override fun register(request: RegistrationRequest) {
        studentService.addActualStudent(
            Login(request.email, request.passwordHash),
            Student(
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
