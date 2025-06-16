package app.web.security

import app.core.repository.user.ITokenRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.baklykov.app.core.model.User
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.SecretKey


/**
 * Service to generate and parse JWT tokens.
 */
@Service
open class JsonWebTokenService(private val settings: JwtSettings, private val repository: ITokenRepository) : JwtTokenService {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Value("\${token.aRefreshTokenDuration}")
    private val refreshTokenDuration: Int? = null

    override fun createToken(user: User): String {
        logger.debug("Generating token for {}", user.email)
        val now = Instant.now()
        return Jwts.builder().issuer(settings.tokenIssuer)
            .issuedAt(Date.from(now))
            .id(user.userId.toString())
            .subject(user.email)
            .expiration(Date.from(now.plus(settings.tokenExpiredIn)))
            .claim(ROLES, user.roles)
            .signWith(getSignInKey(), Jwts.SIG.HS256)
            .compact()
    }

    override fun parseToken(token: String): UserCredentials {
        logger.debug("Parsing token {}", token)

        val key = Keys.hmacShaKeyFor(settings.getTokenSigningKey())

        val claims = Jwts
            .parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .payload

        val subject = claims.subject
        val id = claims.id
        val roles: List<String> = claims.get(ROLES, List::class.java).filterIsInstance<String>()
        return UserCredentialsImpl(id, subject, Collections.unmodifiableList(roles))
    }

    override fun generateRefreshToken(userId: String): String {
        return try {
            val expirationTime = Instant.now().plus(refreshTokenDuration?.toLong() ?: 30, ChronoUnit.DAYS)
            val refreshTokenId = UUID.randomUUID().toString()
            if (repository.checkTokenExists(userId)) {
                repository.removeToken(userId)
            }
            repository.addToken(
                userId = userId,
                token = refreshTokenId,
                expirationTime = expirationTime.toString(),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
            )
            refreshTokenId
        } catch (e: Exception) {
            logger.error("Failed to generate refresh token", e)
            ""
        }
    }

    override fun validateRefreshToken(userId: String, refreshToken: String): Boolean {
        if (repository.checkTokenExists(userId) && repository.checkTokenEnabled(userId)) {
            val expiration = repository.getExpirationTime(userId)?.let { Instant.parse(it) }
            val token = repository.getToken(userId)
            return refreshToken == token && expiration != null && Instant.now().isBefore(expiration)
        }
        return false
    }

    override fun getUserIdByToken(refreshToken: String): String? {
        return try {
            repository.getId(refreshToken)
        } catch (e: Exception) {
            logger.error("Failed to get user ID by refresh token", e)
            null
        }
    }

    private fun getSignInKey(): SecretKey {
        val keyBytes = Decoders.BASE64.decode(settings.getTokenSigningKeyString())
        return Keys.hmacShaKeyFor(keyBytes)
    }

    companion object {
        private const val ROLES = "roles"
    }
}

