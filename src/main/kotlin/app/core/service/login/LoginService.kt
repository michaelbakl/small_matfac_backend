package app.core.service.login

import app.core.exception.LoginFailedException
import app.core.exception.SignUpException
import app.core.security.PasswordEncoder
import app.web.model.Login
import org.springframework.beans.factory.annotation.Value
import org.springframework.transaction.annotation.Transactional
import ru.baklykov.app.core.model.User
import ru.baklykov.app.core.repository.IUserRepository
import java.util.*

open class LoginService(private val userRepository: IUserRepository, private val passwordEncoder: PasswordEncoder) {

    @Value("\${token.aRefreshTokenDuration}")
    private val refreshTokenDuration: Int? = null

    /**
     * login method
     * @param login login of user
     * @return User class
     */
    @Transactional
    open fun login(login: Login): User {
        if (!userRepository.checkLoginExists(login.login)) {
            throw LoginFailedException("Incorrect login")
        }
        val user = userRepository.findByEmail(login.login)
            ?: throw LoginFailedException("User '" + login.login + "' not found")
        if (!passwordEncoder.matches(login.password, user.password ?: "")) {
            throw LoginFailedException("Wrong password")
        }
        return User(user.userId, user.email, user.roles)
    }

    /**
     * login method
     * @param login login of use
     */
    @Transactional
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

        // TODO: add BCrypt Hash
        userRepository.createUser(
            userId,
            login.login,
            login.password
            //BCrypt.hashpw(login.password, BCrypt.gensalt(rounds))
        )
        roles.map { role -> userRepository.createUserRole(userId, role) }
        return userId
    }

}