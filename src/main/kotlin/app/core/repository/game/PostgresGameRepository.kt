package ru.baklykov.app.core.repository.game

import app.core.exception.NotFoundException
import app.core.exception.RepositoryException
import app.core.filter.GameFilter
import app.core.util.SqlQueryBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.stereotype.Repository
import ru.baklykov.app.core.converter.datetime.TimestampzConverter
import app.core.model.game.Game
import app.core.repository.game.IGameRepository
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.RowMapper
import app.core.converter.game.DifficultyConverter
import app.core.converter.game.GameTypeConverter
import app.core.model.game.GameConfig
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.ZonedDateTime
import java.util.*

@Repository
open class PostgresGameRepository(private val jdbcOperations: JdbcOperations) : IGameRepository {

    private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)

    private val qameRowMapper = RowMapper { rs: ResultSet, _: Int ->
        Game(
            gameId = rs.getObject("gameId", UUID::class.java),
            roomId = rs.getObject("roomId", UUID::class.java),
            creatorId = rs.getObject("ownerId", UUID::class.java),
            status = rs.getString("status"),
            config = GameConfig(
                name = rs.getString("name"),
                duration = rs.getInt("duration"),
                gameType = GameTypeConverter.convert(rs.getString("gameType")),
                difficulty = DifficultyConverter.convert(rs.getString("difficulty")),
                allowSkips = rs.getBoolean("allowSkips"),
                enableHints = rs.getBoolean("enableHints"),
                startDate = TimestampzConverter.convert(rs.getTimestamp("startDate")),
                finishDate = TimestampzConverter.convert(rs.getTimestamp("finishDate")),
                questions = getQuestionsByGameId(rs.getObject("gameId", UUID::class.java))
            ),
            questions = emptyList(),
        )
    }

    override fun createGame(game: Game): Int {
        try {
            LOGGER.debug("REPOSITORY create game {}", game)
            val sql =
                "INSERT INTO game (gameId, roomId, teacherId, status) VALUES(?, ?, ?, ?, ?, ?)"
            return jdbcOperations.update(sql, game.gameId, game.roomId, game.config.name, game.creatorId, game.status)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY create game by params {} error", game, e)
            throw RepositoryException("REPOSITORY create game exception", e)
        }
    }

    override fun createGameConfig(gameId: UUID, gameConfig: GameConfig): Int {
        try {
            LOGGER.debug("REPOSITORY create game config {}", gameConfig)
            val sql =
                "INSERT INTO game_config " +
                        "(configId, name, duration, gameType, difficulty, " +
                        "allowSkips, enableHints, startDate, finishDate) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            return jdbcOperations.update(
                sql,
                gameId,
                gameConfig.name,
                gameConfig.duration,
                gameConfig.gameType.toString(),
                gameConfig.difficulty.toString(),
                gameConfig.allowSkips,
                gameConfig.enableHints,
                TimestampzConverter.convertFrom(gameConfig.startDate),
                TimestampzConverter.convertFrom(gameConfig.finishDate)
            )
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY create game config by params {} error", gameConfig, e)
            throw RepositoryException("REPOSITORY create game config exception", e)
        }
    }

    override fun updateGame(game: Game): Int {
        try {
            LOGGER.error("REPOSITORY update game {}", game)
            val sql =
                "UPDATE game SET roomId=?, teacherId=?, status=? WHERE gameId=?) VALUES(?, ?, ?, ?)"
            return jdbcOperations.update(sql, game.roomId, game.creatorId, game.status, game.gameId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY update game by params {} error", game, e)
            throw RepositoryException("REPOSITORY update game exception", e)
        }
    }

    override fun updateGameConfig(gameId: UUID, gameConfig: GameConfig): Int {
        try {
            LOGGER.error("REPOSITORY update game config {}, {}", gameId, gameConfig)
            val sql =
                "UPDATE game_config SET " +
                        "name=?, duration=?, gameType=?, difficulty=?, " +
                        "allowSkips=?, enableHints=?, startDate=?, finishDate=? WHERE configId=?) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            return jdbcOperations.update(
                sql,
                gameConfig.name,
                gameConfig.duration,
                gameConfig.gameType.toString(),
                gameConfig.difficulty.toString(),
                gameConfig.allowSkips,
                gameConfig.enableHints,
                TimestampzConverter.convertFrom(gameConfig.startDate),
                TimestampzConverter.convertFrom(gameConfig.finishDate),
                gameId,
            )
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY update game config by params {} error", gameConfig, e)
            throw RepositoryException("REPOSITORY update game config exception", e)
        }
    }

    override fun getGameById(id: UUID): Game {
        try {
            LOGGER.debug("REPOSITORY get game by id {}", id)
            val sql =
                "SELECT * FROM game WHERE game.gameId=? INNER JOIN game_config ON game.gameId=game_config.configId "
            return jdbcOperations.queryForObject(sql, qameRowMapper, id)
                ?: throw NotFoundException("Game was not found by id")
        } catch (e: Exception) {
            LOGGER.error(
                "REPOSITORY get group by id {} error",
                id,
                e
            )
            throw RepositoryException("REPOSITORY get group by id exception", e)
        }
    }

    override fun getGamesWithParams(filter: GameFilter): List<Game> {
        try {
            LOGGER.debug("REPOSITORY get games by filter {}", filter)

            val sql = SqlQueryBuilder()
                .select("DISTINCT game.*")
                .from("game")
                .apply {
                    filter.questions?.takeIf { it.isNotEmpty() }?.let { questions ->
                        join("INNER", "game_questions", "game.gameId = game_questions.game_id", "1")
                        `in`("game_questions.question_id", questions.map { it.toString() })
                    }
                    join("INNER", "game_config", "game.gameId=game_config.configId", "1")

                    filter.gameId?.let { where("game.gameId", it.toString()) }
                    filter.roomId?.let { where("game.roomId", it.toString()) }
                    filter.creatorId?.let { where("game.creatorId", it.toString()) }
                    filter.status?.let { where("game.status", it) }

                    //filter.questionCount?.let { where("game.status", it) }

                    filter.name?.let { where("game_config.name", it) }
                    filter.duration?.let { where("game_config.duration", it.toString()) }
                    filter.gameType?.let { where("game_config.gameType", it.name) }
                    filter.difficulty?.let { where("game_config.difficulty", it.name) }
                    filter.allowSkips?.let { where("game_config.allowSkips", it.toString()) }
                    filter.enableHints?.let { where("game_config.enableHints", it.toString()) }

                    filter.startDateL?.let { start ->
                        filter.startDateR?.let { end ->
                            between(
                                "game_config.startDate",
                                Timestamp.from(start.toInstant()).toString(),
                                Timestamp.from(end.toInstant()).toString()
                            )
                        } ?: where(
                            "game_config.startDate",
                            Timestamp.from(start.toInstant()).toString(),
                            operand = ">="
                        )
                    }
                    filter.finishDateL?.let { start ->
                        filter.finishDateR?.let { end ->
                            between(
                                "game_config.finishDate",
                                Timestamp.from(start.toInstant()).toString(),
                                Timestamp.from(end.toInstant()).toString()
                            )
                        } ?: where(
                            "game_config.finishDate",
                            Timestamp.from(start.toInstant()).toString(),
                            operand = ">="
                        )
                    }
                    filter.limit?.let { limit(it) }
                    filter.offset?.let { offset(it) }
                }
                .orderBy("game_config.name")
                .build()

            return jdbcOperations.query(
                sql,
                qameRowMapper
            )
        } catch (e: Exception) {
            LOGGER.error(
                "REPOSITORY error getting games by filter {}",
                filter,
                e
            )
            throw RepositoryException("REPOSITORY get games by filter exception", e)
        }
    }

    override fun deleteGameById(id: UUID): Int {
        LOGGER.debug("REPOSITORY remove game {}", id)
        try {
            val sql = "DELETE FROM game WHERE gameId=?"
            LOGGER.debug("REPOSITORY delete game by id: {} sql: {}", id, sql)
            return jdbcOperations.update(sql, id)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t delete game by id {}", id)
            throw RepositoryException("REPOSITORY Can`t delete game by id exception", e)
        }
    }

    override fun updateGameDates(gameId: UUID, startDate: ZonedDateTime, finishDate: ZonedDateTime): Int {
        LOGGER.debug("REPOSITORY update game dates by id {} , {} , {}", gameId, startDate, finishDate)
        try {
            val sql = "UPDATE game_config SET startDate=?, finishDate=? WHERE configId=?"
            LOGGER.debug("REPOSITORY update game dates by id: {} , {} , {} sql: {}", gameId, startDate, finishDate, sql)
            return jdbcOperations.update(
                sql,
                TimestampzConverter.convertFrom(startDate),
                TimestampzConverter.convertFrom(finishDate),
                gameId
            )
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t update game dates by id {} , {} , {}", gameId, startDate, finishDate)
            throw RepositoryException("REPOSITORY Can`t update game dates by id exception", e)
        }
    }

    override fun updateGameStatus(gameId: UUID, status: String): Int {
        LOGGER.debug("REPOSITORY update game status {} , {}", gameId, status)
        try {
            val sql = "UPDATE game SET status=? WHERE gameId=?"
            LOGGER.debug("REPOSITORY update game status by id: {} , {} sql: {}", gameId, status, sql)
            return jdbcOperations.update(sql, status, gameId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t update game status {} , {}", gameId, status)
            throw RepositoryException("REPOSITORY Can`t update game status exception", e)
        }
    }

    override fun addQuestionToGame(gameId: UUID, questionId: UUID): Int {
        LOGGER.debug("REPOSITORY add question to game {} , {}", gameId, questionId)
        try {
            val sql = "INSERT INTO game_questions (game_questions_id, game_id, question_id) VALUES (?, ?, ?)"
            LOGGER.debug("REPOSITORY add question to game by id: {} , {} sql: {}", gameId, questionId, sql)
            return jdbcOperations.update(sql, UUID.randomUUID(), gameId, questionId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t add question to game by id {} , {}", gameId, questionId)
            throw RepositoryException("REPOSITORY Can`t add question to game by id exception", e)
        }
    }

    override fun removeQuestionFromGame(gameId: UUID, questionId: UUID): Int {
        LOGGER.debug("REPOSITORY delete question from game {} , {}", gameId, questionId)
        try {
            val sql = "DELETE FROM game_questions WHERE game_id=? AND question_id=?"
            LOGGER.debug("REPOSITORY delete question from game by id: {} , {} sql: {}", gameId, questionId, sql)
            return jdbcOperations.update(sql, gameId, questionId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t delete question from game by id {} , {}", gameId, questionId)
            throw RepositoryException("REPOSITORY Can`t delete question from game by id exception", e)
        }
    }

    private fun getQuestionsByGameId(gameId: UUID): List<UUID> {
        return try {
            val sql = "SELECT * FROM game_questions WHERE game_id = ?"
            jdbcOperations.query(sql, { rs, _ -> rs.getObject("question_id", UUID::class.java) })
        } catch (e: DataAccessException) {
            emptyList()
        }
    }
}