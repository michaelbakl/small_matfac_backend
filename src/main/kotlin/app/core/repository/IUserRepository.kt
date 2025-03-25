package ru.baklykov.app.core.repository

import ru.baklykov.app.core.model.User
import java.util.*

interface IUserRepository {
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
     * create user
     * @param userId - userId
     * @param email - email
     * @param password - password
     */
    fun createUser(userId: UUID, email: String, password: String): Int

    /**
     * updates user info
     * @param userId - userId
     * @param email - email
     * @param password - password
     */
    fun updateUser(userId: UUID, email: String, password: String): Int

    /**
     * creates user role
     * @param userId: user id
     * @param role: user role
     */
    fun createUserRole(userId: UUID, role: String): Int

    /**
     * removes user role
     * @param userId: user id
     * @param role: user role
     */
    fun removeUserRole(userId: UUID, role: String): Int

    /**
     * removes all user roles
     * @param userId: user id
     */
    fun removeAllUserRoles(userId: UUID): Int

    /**
     * checks if login as email is taken
     * @param email - email
     * @return true if taken
     */
    fun checkLoginExists(email: String): Boolean
}