package app.core.repository.game

import app.core.model.game.GameSession
import java.util.*

interface IGameSessionRepository {
    fun findByGameIdAndStudentId(gameId: UUID, studentId: UUID): GameSession?
    fun createSession(session: GameSession): Int
    fun updateSession(session: GameSession): Int
}
