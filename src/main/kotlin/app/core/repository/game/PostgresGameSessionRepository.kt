package app.core.repository.game

import app.core.exception.RepositoryException
import app.core.model.game.GameSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet
import java.time.ZonedDateTime
import java.util.*

@Repository
@Transactional
open class PostgresGameSessionRepository(private val jdbcOperations: JdbcOperations) : IGameSessionRepository {
    private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)

    private val gameSessionRowMapper = { rs: ResultSet, i: Int ->
        GameSession(
            sessionId = UUID.fromString(rs.getString("session_id")),
            gameId = UUID.fromString(rs.getString("game_id")),
            studentId = UUID.fromString(rs.getString("student_id")),
            startedAt = rs.getObject("started_at", ZonedDateTime::class.java),
            finishedAt = rs.getObject("finished_at", ZonedDateTime::class.java),
            currentQuestionIndex = rs.getInt("current_question_index")
        )
    }

    override fun findByGameIdAndStudentId(gameId: UUID, studentId: UUID): GameSession? {
        LOGGER.debug("REPOSITORY find session by game id and student id {}, {}", gameId, studentId)
        try {
            val sql = """
            SELECT * FROM game_session 
            WHERE game_id = ? AND student_id = ?
            """.trimIndent()
            return jdbcOperations.query(sql, gameSessionRowMapper, gameId, studentId)
                .firstOrNull()
        } catch (e: DataAccessException) {
            LOGGER.error("REPOSITORY find session by game id and student id {}, {}", gameId, studentId)
            throw RepositoryException("REPOSITORY find session by game id and student id exception", e)
        }
    }

    override fun createSession(session: GameSession): Int {
        LOGGER.debug("REPOSITORY create session {}", session)
        try {
            val sql = """
            INSERT INTO game_session (session_id, game_id, student_id, started_at, current_question_index) 
            VALUES (?, ?, ?, ?, ?)
            """.trimIndent()
            return jdbcOperations.update(
                sql,
                session.sessionId,
                session.gameId,
                session.studentId,
                session.startedAt,
                session.currentQuestionIndex
            )
        } catch (e: DataAccessException) {
            LOGGER.error("REPOSITORY find session by game id and student id {}", session)
            throw RepositoryException("REPOSITORY find session by game id and student id exception", e)
        }
    }

    override fun updateSession(session: GameSession): Int {
        LOGGER.debug("REPOSITORY update session {}", session)
        try {
            val sql = """
            UPDATE game_session 
            SET finished_at = ?, current_question_index = ? 
            WHERE session_id = ?
            """.trimIndent()
            return jdbcOperations.update(
                sql,
                session.finishedAt,
                session.currentQuestionIndex,
                session.sessionId
            )
        } catch (e: DataAccessException) {
            LOGGER.error("REPOSITORY update session {}", session)
            throw RepositoryException("REPOSITORY update session exception", e)
        }
    }

}