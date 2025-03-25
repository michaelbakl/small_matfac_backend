package ru.baklykov.app.core.repository.game

import ru.baklykov.app.core.model.game.Game
import ru.baklykov.app.core.model.game.GameType
import java.time.ZonedDateTime
import java.util.*

interface IGameRepository {

    /**
     * adds game to database
     * @param game - game to add
     * @return added game
     */
    fun addGame(game: Game): Game

    /**
     * updates game information
     * @param game - game with new information
     * @return updated game with new info
     */
    fun updateGame(game: Game): Game

    /**
     * finds game by id
     * @param id - UUID of the game
     * @return found game or null
     */
    fun getGameById(id: UUID): Game

    /**
     * @param id - game id
     * @param roomId - id  of the room which in game is being played
     * @param name - game name
     * @param status - game status
     * @param gameType - type of the game
     * @param startDate - date of starting the game (game is available to play)
     * @param finishDate - date of closing the game (becomes unavailable)
     */
    fun getGamesWithParams(
        id: UUID?,
        roomId: UUID?,
        name: String?,
        status: String?,
        gameType: GameType?,
        startDate: ZonedDateTime?,
        finishDate: ZonedDateTime?
    )

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
     * @param roomId - id of the room
     * @param status - new status of the game
     * @return 1 if updated, 0 otherwise
     */
    fun updateGameStatus(roomId: UUID, status: String): Int

    /**
     * adds question to game
     * @param gameId - id of the room
     * @param questionId - question id to add to the room
     * @return 1 if added, 0 otherwise
     */
    fun addQuestionToGame(gameId: UUID, questionId: UUID): Int

    /**
     * adds questions to the room
     * @param roomId - id of the room
     * @param questionId - question id to remove to the room
     * @return updated room
     */
    fun removeQuestionFromGame(roomId: UUID, questionId: UUID): Game

    /**
     * makes game available to all players in the room, game is played in
     * @param gameId - id of the game to start the game
     * @return 1 if success, 0 otherwise
     */
    fun startGame(gameId: UUID): Int
}