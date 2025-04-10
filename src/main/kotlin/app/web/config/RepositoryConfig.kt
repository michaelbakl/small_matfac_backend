package app.web.config

import app.core.repository.group.IGroupRepository
import app.core.repository.group.PostgresGroupRepository
import app.core.repository.student.IStudentRepository
import app.core.repository.student.PostgresStudentRepository
import app.core.repository.subject.ISubjectRepository
import app.core.repository.subject.PostgresSubjectRepository
import app.core.repository.teacher.ITeacherRepository
import app.core.repository.teacher.PostgresTeacherRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcOperations
import ru.baklykov.app.core.repository.answer.IAnswerRepository
import ru.baklykov.app.core.repository.answer.PostgresAnswerRepository
import ru.baklykov.app.core.repository.picture.IPictureRepository
import ru.baklykov.app.core.repository.picture.PostgresPictureRepository
import ru.baklykov.app.core.repository.question.IQuestionRepository
import ru.baklykov.app.core.repository.question.PostgresQuestionRepository

@Configuration
open class RepositoryConfig {

    @Bean
    open fun subjectRepository(jdbcOperations: JdbcOperations): ISubjectRepository {
        return PostgresSubjectRepository(jdbcOperations)
    }

    @Bean
    open fun studentRepository(jdbcOperations: JdbcOperations): IStudentRepository {
        return PostgresStudentRepository(jdbcOperations)
    }

    @Bean
    open fun teacherRepository(jdbcOperations: JdbcOperations): ITeacherRepository {
        return PostgresTeacherRepository(jdbcOperations)
    }

    @Bean
    open fun groupRepository(jdbcOperations: JdbcOperations): IGroupRepository {
        return PostgresGroupRepository(jdbcOperations)
    }

    @Bean
    open fun questionRepository(jdbcOperations: JdbcOperations): IQuestionRepository {
        return PostgresQuestionRepository(jdbcOperations)
    }

    @Bean
    open fun answerRepository(jdbcOperations: JdbcOperations): IAnswerRepository {
        return PostgresAnswerRepository(jdbcOperations)
    }

    @Bean
    open fun pictureRepository(jdbcOperations: JdbcOperations): IPictureRepository {
        return PostgresPictureRepository(jdbcOperations)
    }

}
