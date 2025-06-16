package app.core.service.login

import app.core.repository.user.ITokenRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*


@Service
open class RefreshTokenService(private val repository: ITokenRepository) {

    @Value("\${token.aRefreshTokenDuration}")
    private val refreshTokenDuration: Int? = null

    @Transactional
    open fun generateRefreshToken(userId: String?): String? {
        return try {
            val expirationTime = Instant.now().plus(refreshTokenDuration?.toLong()?: 30, ChronoUnit.HOURS)
            val refreshTokenId: String = UUID.randomUUID().toString()
            if (repository.checkTokenExists(userId!!)) {
                repository.removeToken(userId)
            }
            repository.addToken(userId, refreshTokenId, expirationTime.toString(), LocalDateTime.now(), LocalDateTime.now())
            refreshTokenId
        } catch (e: Exception) {
            ""
        }
    }

    fun validateRefreshToken(userId: String?, refreshToken: String): Boolean {
        userId?. let {
            if (repository.checkTokenExists(it) && repository.checkTokenEnabled(it)) {
                val time = repository.getExpirationTime(it)
                val token = repository.getToken(it)
                val expirationTime = Instant.parse(time)
                if (refreshToken == token) {
                    return Instant.now().isBefore(expirationTime)
                }
            }
        }
        return false
    }

    fun getUserIdByToken(refreshToken: String?): String? {
        return try {
            repository.getId(refreshToken!!)
        } catch (e: Exception) {
            ""
        }
    }

}