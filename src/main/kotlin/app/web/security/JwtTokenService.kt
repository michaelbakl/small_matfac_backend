package app.web.security

import ru.baklykov.app.core.model.User

/**
 * jwt token service class
 */
interface JwtTokenService {
    /**
     * Creates new Token for user.
     * @param user contains User to be represented as token
     * @return signed token
     */
    fun createToken(user: User): String

    /**
     * Parses the token
     * @param token the token string to parse
     * @return authenticated data
     */
    fun parseToken(token: String): UserCredentials

    /**
     * generates refresh token for user
     * @param userId: user id
     * @return refresh token string
     */
    fun generateRefreshToken(userId: String): String

    /**
     * validates user`s refresh token
     * @param userId: id of the user (owner of the refresh token)
     * @param refreshToken: user`s refresh token
     * @return true if token is valid, false otherwise
     */
    fun validateRefreshToken(userId: String, refreshToken: String): Boolean

    /**
     * returns userId by user`s refresh token
     * @param refreshToken: user`s refresh token
     * @return id of the owner of the refresh token
     */
    fun getUserIdByToken(refreshToken: String): String?

}

