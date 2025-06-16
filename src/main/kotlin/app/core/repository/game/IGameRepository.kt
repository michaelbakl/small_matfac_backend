package app.core.repository.game

import app.core.filter.GameFilter
import app.core.model.game.Game
import app.core.model.game.GameConfig
import java.time.ZonedDateTime
import java.util.*

interface IGameRepository {

    /**
     * adds game to database
     * @param game - game to add
     * @return added game
     */
    fun createGame(game: Game): Int

    /**
     * adds game configuration to the database
     * @param gameId - id of the game
     * @param gameConfig - game configuration
     * @return 1 if added, 0 otherwise
     */
    fun createGameConfig(gameId: UUID, gameConfig: GameConfig): Int

    /**
     * updates game information
     * @param game - game with new information
     * @return updated game with new info
     */
    fun updateGame(game: Game): Int

    /**
     * updates game configuration
     * @param gameId - id of the game
     * @param gameConfig - game configuration with new information
     * @return updated game configuration with new info
     */
    fun updateGameConfig(gameId: UUID, gameConfig: GameConfig): Int

    /**
     * finds game by id
     * @param id - UUID of the game
     * @return found game or null
     */
    fun getGameById(id: UUID): Game

    /**
     * @param filter - contains information for games
     */
    fun getGamesWithParams(filter: GameFilter): List<Game>

    /**
     * deletes game by id
     * @param id - id of the game
     * @return 1 if deleted, 0 if not
     */
    fun deleteGameById(id: UUID): Int

    /**
     * updates game dates of starting and finishing
     * @param gameId - id of the game
     * @param startDate - date of starting the room
     * @param finishDate - date when room become unavailable
     * @return 1 if success, 0 otherwise
     */
    fun updateGameDates(gameId: UUID, startDate: ZonedDateTime, finishDate: ZonedDateTime): Int

    /**
     * updates game status
     * @param gameId - id of the game
     * @param status - new status of the game
     * @return 1 if updated, 0 otherwise
     */
    fun updateGameStatus(gameId: UUID, status: String): Int

    /**
     * adds question to game
     * @param gameId - id of the game
     * @param questionId - question id to add to the room
     * @return 1 if added, 0 otherwise
     */
    fun addQuestionToGame(gameId: UUID, questionId: UUID): Int

    /**
     * adds questions to the game
     * @param gameId - id of the game
     * @param questionId - question id to remove to the game
     * @return 1 if removed, 0 otherwise
     */
    fun removeQuestionFromGame(gameId: UUID, questionId: UUID): Int

}
