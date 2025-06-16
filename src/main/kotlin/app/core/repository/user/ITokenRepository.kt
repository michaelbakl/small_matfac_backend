package app.core.repository.user

import java.time.LocalDateTime

interface ITokenRepository {
    /**
     * adds token to database
     * @param userId: user id
     * @param token: jwt token
     * @return 1 if added, 0 if not
     */
    fun addToken(userId: String, token: String, expirationTime: String, createdAt: LocalDateTime, updatedAt: LocalDateTime): Int

    /**
     * updates token in repository
     * @param userId: user id
     * @param token: jwt token
     * @param updatedAt: time when the token was updated
     * @return 1 if updated, 0 if not
     */
    fun refreshToken(userId: String, token: String, expirationTime: String, updatedAt: LocalDateTime): Int

    /**
     * removes token from repository
     * @param userId - user id
     * @return 1 if removed, 0 if not
     */
    fun removeToken(userId: String): Int

    /**
     * checks if token for user exists
     * @param userId - user id
     * @return true if exists, false if not
     */
    fun checkTokenExists(userId: String): Boolean

    /**
     * checks if token for user enabled
     * @param userId - user id
     * @return true if enabled, false if not
     */
    fun checkTokenEnabled(userId: String): Boolean

    /**
     * returns token id
     * @param userId - user id
     * @return - token id in string
     */
    fun getToken(userId: String): String?

    /**
     * returns expiration time string for token by user id
     * @param userId - user id
     * @return expiration time in string (Instance class object stringified)
     */
    fun getExpirationTime(userId: String): String?

    /**
     * returns user id by refresh token
     * @param token - refresh token
     * @return user id
     */
    fun getId(token: String): String?

}