package app.core.service.login

import app.core.exception.LoginFailedException
import app.core.exception.RepositoryException
import app.core.exception.SignUpException
import app.core.handler.registration.RoleRegistrationHandler
import app.core.model.person.RegistrationRequest
import app.core.model.request.RequestStatus
import app.core.repository.admin.IRequestsRepository
import app.core.repository.role.IRoleRepository
import app.core.security.PasswordEncoder
import app.web.model.Login
import app.web.model.request.login.SignUpRegistrationRequest
import org.mindrot.jbcrypt.BCrypt
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.baklykov.app.core.model.User
import ru.baklykov.app.core.repository.user.IUserRepository
import java.time.Instant
import java.time.ZonedDateTime
import java.util.*

@Service
open class LoginService(
    private val userRepository: IUserRepository,
    private val registrationRequestRepo: IRequestsRepository,
    private val roleRepository: IRoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val handlers: List<RoleRegistrationHandler>
) {

    @Value("\${token.aRefreshTokenDuration}")
    private val refreshTokenDuration: Int? = null

    /**
     * submission method
     * @param req request for registration
     */
    @Transactional(rollbackFor = [IllegalStateException::class, IllegalArgumentException::class, RepositoryException::class])
    open fun submitRequest(req: SignUpRegistrationRequest) {
        if (registrationRequestRepo.existsByEmail(req.email)) {
            throw IllegalStateException("Request already submitted")
        }

        val request = RegistrationRequest(
            id = UUID.randomUUID(),
            email = req.email,
            passwordHash = req.password,
            surname = req.surname,
            name = req.name,
            middleName = req.middleName,
            requestedRole = req.requestedRole.uppercase(),
            status = "PENDING",
            submittedAt = ZonedDateTime.now().toInstant()
        )
        val requiresApproval = roleRepository.requiresApproval(request.requestedRole)

        if (requiresApproval) {
            registrationRequestRepo.save(request)
        } else {
            val handler = handlers.find { it.supports(request.requestedRole) }
                ?: throw IllegalStateException("No handler found for role: ${request.requestedRole}")

            handler.register(request)

            request.status = RequestStatus.APPROVED.name
            request.reviewedAt = Instant.now()
            registrationRequestRepo.save(request)
        }
    }

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