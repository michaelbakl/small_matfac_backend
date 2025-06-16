package ru.baklykov.app.core.repository.user

import app.core.exception.RepositoryException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import ru.baklykov.app.core.model.User
import java.sql.ResultSet
import java.util.*

@Repository
open class PostgresUserRepository(private val jdbcOperations: JdbcOperations): IUserRepository {

    private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)

    private val rowMapper = { resultSet: ResultSet, i: Int ->
        User(
            UUID.fromString(resultSet.getString("userId")),
            resultSet.getString("username"),
            getRoles(resultSet.getString("userId")),
            resultSet.getString("password")
        )
    }

    override fun findById(id: UUID): User? {
        try {
            LOGGER.debug("REPOSITORY get user by id {}", id)
            return jdbcOperations.queryForObject("SELECT * FROM \"user\" WHERE userId = ?", rowMapper, id)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY error getting user by id {}", id, e)
            throw RepositoryException("REPOSITORY can`t get user by id exception", e)
        }
    }

    override fun findByEmail(email: String): User? {
        try {
            LOGGER.debug("REPOSITORY get user by email {}", email)
            return jdbcOperations.queryForObject("SELECT * FROM \"user\" WHERE username = ?", rowMapper, email)
        } catch (e: Exception) {
            LOGGER.debug("REPOSITORY error getting user by email {}", email, e)
            throw RepositoryException("REPOSITORY can`t find user by email exception", e)
        }
    }

    override fun findAll(): List<User> {
        try {
            LOGGER.debug("REPOSITORY get all users")
            return jdbcOperations.query("SELECT * FROM \"user\"", rowMapper)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY error getting all users", e)
            throw RepositoryException("REPOSITORY can`t get all users exception", e)
        }
    }

    override fun createUser(userId: UUID, email: String, password: String): Int {
        try {
            LOGGER.debug("REPOSITORY create user by params {}, {}", userId, email)
            return jdbcOperations.update("INSERT INTO \"user\" VALUES (?, ?, ?)", userId, email, password)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY error creating user by params {}, {}", userId, email, e)
            throw RepositoryException("REPOSITORY can`t create user exception", e)
        }
    }

    override fun updateUser(userId: UUID, email: String, password: String): Int {
        try {
            LOGGER.debug("REPOSITORY update user by params {}, {}", userId, email)
            return jdbcOperations.update("UPDATE \"user\" SET email = ?, password = ? WHERE userId = ?", email, password, userId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY error updating user by params {}, {}", userId, email, e)
            throw RepositoryException("REPOSITORY can`t update user exception", e)
        }
    }

    override fun createUserRole(userId: UUID, role: String): Int {
        try {
            LOGGER.debug("REPOSITORY create user role by params {}, {}", userId, role)
            return jdbcOperations.update("INSERT INTO user_roles VALUES (?, ?)", userId, role)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY error creating user role by params {}, {}", userId, role, e)
            throw RepositoryException("REPOSITORY can`t create user role exception", e)
        }
    }

    override fun removeUserRole(userId: UUID, role: String): Int {
        return try {
            LOGGER.debug("REPOSITORY remove user role by params {}, {}", userId, role)
            jdbcOperations.update("DELETE FROM user_roles WHERE userId = ? AND role = ?", userId, role)
        } catch (e: Exception) {
            1
        }
    }

    override fun removeAllUserRoles(userId: UUID): Int {
        return try {
            LOGGER.debug("REPOSITORY remove all user roles by id {}", userId)
            jdbcOperations.update("DELETE FROM user_roles WHERE userId = ?", userId)
        } catch (e: Exception) {
            1
        }
    }

    override fun checkLoginExists(email: String): Boolean {
        return try {
            val checker = jdbcOperations.queryForObject("SELECT COUNT(username) AS counter FROM \"user\" WHERE username =?",
                { resultSet: ResultSet, i: Int -> resultSet.getInt("counter") }, email
            )
            checker == 1
        } catch (e: Exception) {
            false
        }
    }

    private fun getRoles(userId: String): List<String> {
        return jdbcOperations.query("SELECT * FROM user_roles WHERE userId = ?",
            { resultSet: ResultSet, i: Int -> resultSet.getString("role") }, UUID.fromString(userId)
        )
    }
}