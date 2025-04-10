package ru.baklykov.app.core.repository.game

import app.core.exception.RepositoryException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcOperations
import ru.baklykov.app.core.model.GroupInfo
import ru.baklykov.app.core.model.game.Game
import ru.baklykov.app.core.model.game.GameConfig
import ru.baklykov.app.core.model.game.GameType
import java.sql.ResultSet
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class PostgresGameRepository(private val jdbcOperations: JdbcOperations): IGameRepository {

    private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)

    override fun createGame(game: Game): Int {
        try {
            LOGGER.error("REPOSITORY create game {}", game)
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
            LOGGER.error("REPOSITORY create game config {}", gameConfig)
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
                gameConfig.startDate,
                gameConfig.finishDate
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
                gameConfig.startDate,
                gameConfig.finishDate,
                gameId,
            )
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY update game config by params {} error", gameConfig, e)
            throw RepositoryException("REPOSITORY update game config exception", e)
        }
    }

    override fun getGameById(id: UUID): Game {
        try {
            LOGGER.error("REPOSITORY get game by id {}", id)
            val sql = "SELECT * FROM game WHERE gameId=?"
            return jdbcOperations.queryForObject(sql, { resultSet: ResultSet, i: Int ->
                Game(
                    UUID.fromString(resultSet.getString("gameId")),
                    UUID.fromString(resultSet.getString("roomId")),
                    UUID.fromString(resultSet.getString("creatorId")),
                    getQuestionsInGame(resultSet.getString("gameId")),
                    //TODO
                    resultSet.getString("status"),
                )
            }, id)
        } catch (e: Exception) {
            LOGGER.error(
                "REPOSITORY get group by id {} error",
                groupId,
                e
            )
            throw RepositoryException("REPOSITORY get group by id exception", e)
        }
    }

    override fun getGamesWithParams(
        id: UUID?,
        roomId: UUID?,
        name: String?,
        status: String?,
        gameType: GameType?,
        startDate: ZonedDateTime?,
        finishDate: ZonedDateTime?
    ): List<Game> {
        TODO("Not yet implemented")
    }

    override fun deleteGameById(id: UUID): Int {
        TODO("Not yet implemented")
    }

    override fun updateGameDates(gameId: UUID, startDate: ZonedDateTime, finishDate: ZonedDateTime): Int {
        TODO("Not yet implemented")
    }

    override fun updateGameStatus(gameId: UUID, status: String): Int {
        TODO("Not yet implemented")
    }

    override fun addQuestionToGame(gameId: UUID, questionId: UUID): Int {
        TODO("Not yet implemented")
    }

    override fun removeQuestionFromGame(roomId: UUID, questionId: UUID): Game {
        TODO("Not yet implemented")
    }

    override fun startGame(gameId: UUID): Int {
        TODO("Not yet implemented")
    }
}