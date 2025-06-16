package app.core.repository.user

import app.core.exception.RepositoryException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.stereotype.Repository
import java.lang.Boolean.TRUE
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*

@Repository
open class PostgresTokenRepository(private val jdbcOperations: JdbcOperations) : ITokenRepository {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun addToken(userId: String, token: String, expirationTime: String, createdAt: LocalDateTime, updatedAt: LocalDateTime): Int {
        logger.debug("REPOSITORY add token to repository {}, {}", userId, token)
        return try {
            val sql = "INSERT INTO refresh_tokens (userId, token, enabled, expirationTime, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)"
            jdbcOperations.update(sql, UUID.fromString(userId), token, true, expirationTime, Timestamp.valueOf(createdAt), Timestamp.valueOf(updatedAt))
        } catch (e: java.lang.Exception) {
            logger.debug("REPOSITORY can`t add token to repository {}, {}", userId, token)
            throw RepositoryException("REPOSITORY add token exception", e)
        }

    }

    override fun refreshToken(userId: String, token: String, expirationTime: String, updatedAt: LocalDateTime): Int {
        logger.debug("REPOSITORY update token to repository by user id {}, {}", userId, token)
        return try {
            val sql = "UPDATE refresh_tokens SET token = ?, enabled = ?, expirationTime = ?, updated_at = ? WHERE userId = ?"
            jdbcOperations.update(sql, token, true, expirationTime, Timestamp.valueOf(updatedAt), UUID.fromString(userId))
        } catch (e: Exception) {
            logger.debug("REPOSITORY can`t update token to repository by user id {}, {}", userId, token)
            throw RepositoryException("REPOSITORY update token by user id exception", e)
        }

    }

    override fun removeToken(userId: String): Int {
        logger.debug("REPOSITORY remove token from repository by user id {}", userId)
        return try {
            val sql = "DELETE FROM refresh_tokens WHERE userId = ?"
            jdbcOperations.update(sql, UUID.fromString(userId))
        } catch (e: java.lang.Exception) {
            logger.debug("REPOSITORY can`t remove token from repository by user id {}", userId)
            throw RepositoryException("REPOSITORY remove token by user id exception", e)
        }

    }

    override fun checkTokenExists(userId: String): Boolean {
        return try {
            val checker =
                jdbcOperations.queryForObject(
                    "SELECT COUNT(userId) AS counter FROM refresh_tokens WHERE userId =?",
                    { resultSet: ResultSet, i: Int -> resultSet.getInt("counter") }, UUID.fromString(userId)
                )
            checker == 1
        } catch (e: Exception) {
            false
        }
    }

    override fun checkTokenEnabled(userId: String): Boolean {
        return try {
            TRUE == jdbcOperations.queryForObject(
                "SELECT enabled FROM refresh_tokens WHERE userId =?",
                { resultSet: ResultSet, i: Int -> resultSet.getBoolean("enabled") }, UUID.fromString(userId)
            )
        } catch (e: Exception) {
            false
        }
    }

    override fun getToken(userId: String): String? {
        return try {
            jdbcOperations.queryForObject(
                "SELECT token FROM refresh_tokens WHERE userId =?",
                { resultSet: ResultSet, i: Int -> resultSet.getString("token") }, UUID.fromString(userId)
            )
        } catch (e: Exception) {
            throw RepositoryException("REPOSITORY get token by user id exception", e)
        }

    }

    override fun getExpirationTime(userId: String): String? {
        return try {
            jdbcOperations.queryForObject(
                "SELECT expirationTime FROM refresh_tokens WHERE userId =?",
                { resultSet: ResultSet, i: Int -> resultSet.getString("expirationTime") }, UUID.fromString(userId)
            )
        } catch (e: Exception) {
            throw RepositoryException("REPOSITORY get expiration time for token by user id exception", e)
        }

    }

    override fun getId(token: String): String? {
        return try {
            jdbcOperations.queryForObject(
                "SELECT userId FROM refresh_tokens WHERE token = ?",
                { resultSet: ResultSet, i: Int -> resultSet.getString("userId") }, token
            )
        } catch (e: Exception) {
            throw RepositoryException("REPOSITORY get user id by token exception", e)
        }

    }
}