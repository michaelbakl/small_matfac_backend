package app.core.service.user

import ru.baklykov.app.core.model.User
import java.util.*

interface IUserService {

    /**
     * finds user by his id
     * @param id - id of user
     * @return User
     */
    fun findById(id: UUID): User?

    /**
     * finds user by his email
     * @param email - email of user
     * @return User
     */
    fun findByEmail(email: String): User?

    /**
     * returns all users
     * @return list of users
     */
    fun findAll(): List<User>

    /**
     * create user with base roles
     * @param email: email
     * @param password: password
     * @param roles: list of user roles
     */
    fun createUser (email: String, password: String, roles: List<String>): UUID

    /**
     * checks if login as email is taken
     * @param email - email
     * @return true if taken
     */
    fun checkLoginExists(email: String): Boolean

    /**
     * grants privileges to user
     * @param userId: id of tje user
     * @param privileges: list with roles
     * @return 1 if succeeded, 0 - otherwise
     */
    fun grantUserPrivileges(userId: UUID, privileges: List<String>): Int

    /**
     * updates privileges to user
     * @param userId: id of tje user
     * @param privileges: list with roles
     * @return 1 if succeeded, 0 - otherwise
     */
    fun updateUserPrivileges(userId: UUID, privileges: List<String>): Int

    /**
     * removes privileges to user
     * @param userId: id of tje user
     * @return 1 if succeeded, 0 - otherwise
     */
    fun removeAllUserPrivileges(userId: UUID): Int

    /**
     * grants privileges to user
     * @param userId: id of tje user
     * @param privilegeToRemove: list with roles
     * @return 1 if succeeded, 0 - otherwise
     */
    fun removeUserPrivilege(userId: UUID, privilegeToRemove: String): Int

}