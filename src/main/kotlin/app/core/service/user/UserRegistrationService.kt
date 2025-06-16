package app.core.service.user


import app.core.exception.SignUpException
import app.web.model.Login
import org.mindrot.jbcrypt.BCrypt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.baklykov.app.core.repository.user.IUserRepository
import java.util.UUID

@Service
open class UserRegistrationService(
    private val userRepository: IUserRepository
) {

    @Transactional(rollbackFor = [SignUpException::class])
    open fun create(login: Login, roles: List<String>): UUID {
        val firstBorder = 5
        val rounds = 12
        val secondBorder = 51
        if (
            (login.login.length < firstBorder || login.login.length > secondBorder) ||
            (login.password.length < firstBorder || login.password.length > secondBorder)
        ) {
            throw SignUpException("Wrong inputs!")
        }
        if (userRepository.checkLoginExists(login.login)) {
            throw SignUpException("This login is taken: " + login.login)
        }
        val userId: UUID = UUID.randomUUID()

        userRepository.createUser(
            userId,
            login.login,
            BCrypt.hashpw(login.password, BCrypt.gensalt(rounds))
        )
        roles.map { role -> userRepository.createUserRole(userId, role) }
        return userId
    }
}
