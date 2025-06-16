package app.web.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Duration

/**
 * Settings to the JWT token.
 */
@Component
class JwtSettings{
    @Value("\${jwt.issuer}")
    val tokenIssuer: String = ""

    @Value("\${jwt.signingKey}")
    private val tokenSigningKey: String = ""

    @Value("\${jwt.aTokenDuration}")
    private val aTokenDuration: Long = 0


    fun getTokenSigningKey(): ByteArray {
        return tokenSigningKey.toByteArray(StandardCharsets.UTF_8)
    }

    fun getTokenSigningKeyString(): String {
        return tokenSigningKey
    }

    val tokenExpiredIn: Duration
        get() = Duration.ofMinutes(aTokenDuration)
}




