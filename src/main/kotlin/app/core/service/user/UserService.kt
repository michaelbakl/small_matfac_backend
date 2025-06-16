package app.core.service.user

import app.core.exception.RepositoryException
import app.core.exception.ServiceException
import app.core.exception.SignUpException
import org.mindrot.jbcrypt.BCrypt
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.baklykov.app.core.model.User
import ru.baklykov.app.core.repository.user.IUserRepository
import java.util.*

@Service
open class UserService(private val repository: IUserRepository): IUserService {

    private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)

    override fun findById(id: UUID): User? {
        try {
            LOGGER.debug("SERVICE find user by id {}", id)
            return repository.findById(id)
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error finding user by id {}", id)
            throw ServiceException("SERVICE find user by id exception", e)
        }
    }

    override fun findByEmail(email: String): User? {
        try {
            LOGGER.debug("SERVICE find user by email {}", email)
            return repository.findByEmail(email)
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE find user by email {}", email)
            throw ServiceException("SERVICE find user by email exception", e)
        }
    }

    override fun findAll(): List<User> {
        try {
            LOGGER.debug("SERVICE get all users")
            return repository.findAll()
        } catch (e: RepositoryException) {
            throw ServiceException("SERVICE get all users exception", e)
        }
    }

    @Transactional
    override fun createUser(email: String, password: String, roles: List<String>): UUID {
        try {
            LOGGER.debug("SERVICE create user")
            val firstBorder = 5
            val rounds = 12
            val secondBorder = 51
            if (
                (email.length < firstBorder || email.length > secondBorder) ||
                (password.length < firstBorder || password.length > secondBorder)
            ) {
                throw SignUpException("Wrong inputs!")
            }
            if (repository.checkLoginExists(email)) {
                throw SignUpException("This login is taken: $email")
            }
            val userId: UUID = UUID.randomUUID()
            repository.createUser(
                userId,
                email,
                BCrypt.hashpw(password, BCrypt.gensalt(rounds))
            )
            roles.map { role -> repository.createUserRole(userId, role) }
            return userId
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error creating user")
            throw ServiceException("SERVICE create user exception", e)
        }
    }

    override fun checkLoginExists(email: String): Boolean {
        try {
            LOGGER.debug("SERVICE check login exists {}", email)
            return repository.checkLoginExists(email)
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error checking login exists {}", email, e)
            throw ServiceException("SERVICE checking login exist error", e)
        }
    }

    @Transactional
    override fun grantUserPrivileges(userId: UUID, privileges: List<String>): Int {
        try {
            LOGGER.debug("SERVICE grant privileges to user {}, {}", userId, privileges)
            privileges.map { item -> repository.createUserRole(userId, item) }
            return 1
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error granting privileges to user {}, {}", userId, privileges, e)
            throw ServiceException("SERVICE grant privileges to user exception", e)
        }
    }

    @Transactional
    override fun updateUserPrivileges(userId: UUID, privileges: List<String>): Int {
        try {
            LOGGER.debug("SERVICE update privileges to user {}, {}", userId, privileges)
            repository.removeAllUserRoles(userId)
            privileges.map { item -> repository.createUserRole(userId, item) }
            return 1
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error updating privileges to user {}, {}", userId, privileges, e)
            throw ServiceException("SERVICE update privileges to user exception", e)
        }
    }

    @Transactional
    override fun removeAllUserPrivileges(userId: UUID): Int {
        try {
            LOGGER.debug("SERVICE remove all privileges to user {}", userId)
            return repository.removeAllUserRoles(userId)
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error removing all privileges to user {}", userId, e)
            throw ServiceException("SERVICE remove all privileges to user exception", e)
        }
    }

    @Transactional
    override fun removeUserPrivilege(userId: UUID, privilegeToRemove: String): Int {
        try {
            LOGGER.debug("SERVICE remove privilege to user {}, {}", userId, privilegeToRemove)
            return repository.removeUserRole(userId, privilegeToRemove)
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error removing privilege to user {}, {}", userId, privilegeToRemove, e)
            throw ServiceException("SERVICE remove privilege to user exception", e)
        }
    }

}
