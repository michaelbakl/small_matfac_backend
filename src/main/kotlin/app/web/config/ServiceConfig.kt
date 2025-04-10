package app.web.config

import app.core.repository.group.IGroupRepository
import app.core.repository.student.IStudentRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import app.core.repository.subject.ISubjectRepository
import app.core.repository.teacher.ITeacherRepository
import app.core.security.PasswordEncoder
import app.core.service.group.GroupService
import app.core.service.group.IGroupService
import app.core.service.login.LoginService
import app.core.service.student.IStudentService
import app.core.service.student.StudentService
import app.core.service.subject.ISubjectService
import app.core.service.subject.SubjectService
import app.core.service.teacher.ITeacherService
import app.core.service.teacher.TeacherService
import ru.baklykov.app.core.repository.IUserRepository
import ru.baklykov.app.core.repository.game.IGameRepository
import ru.baklykov.app.core.repository.room.IRoomRepository
import ru.baklykov.app.core.service.room.IRoomService
import ru.baklykov.app.core.service.room.RoomService

@Configuration
open class ServiceConfig {

    @Bean
    open fun subjectService(subjectRepository: ISubjectRepository): ISubjectService {
        return SubjectService(subjectRepository)
    }

    @Bean
    open fun loginService(userRepository: IUserRepository, passwordEncoder: PasswordEncoder): LoginService {
        return LoginService(userRepository, passwordEncoder)
    }

    @Bean
    open fun teacherService(teacherRepository: ITeacherRepository, loginService: LoginService): ITeacherService {
        return TeacherService(teacherRepository, loginService)
    }

    @Bean
    open fun studentService(studentRepository: IStudentRepository, loginService: LoginService): IStudentService {
        return StudentService(studentRepository, loginService)
    }

    @Bean
    open fun groupService(repository: IGroupRepository): IGroupService {
        return GroupService(repository)
    }

    @Bean
    open fun roomService(roomRepository: IRoomRepository, gameRepository: IGameRepository): IRoomService {
        return RoomService(roomRepository, gameRepository)
    }

}
