package ru.baklykov.app.core.service.game

import ru.baklykov.app.core.model.game.GameConfig
import ru.baklykov.app.web.model.response.game.GetGameInfoResponse

interface IGameService {

    /**
     * starts game in the room, players can start playing the game
     * @param roomId - uuid of the room
     * @param gameId - uuid of the game
     * @return game info response
     */
    fun startGameInRoom(roomId: String, gameId: String): GetGameInfoResponse

    /**
     * creates game by game configuration
     * @param gameConfig - game configuration
     * @return game info response
     */
    fun createGame(gameConfig: GameConfig): GetGameInfoResponse

    /**
     * deletes game from the server. Game is removed from every room it is played
     * @param gameId - uuid of the game
     * @return 1 if deleted, 0 otherwise
     */
    fun deleteGameById(gameId: String): Int

    /**
     * adds questions to the game
     * @param questions - list of questions ids to add to the game
     * @return game info response
     */
    fun addQuestionsToTheGame(questions: List<String>): GetGameInfoResponse

    /**
     * checks if game is started
     * @param roomId - uuid of the room
     * @param gameId - uuid of the game
     * @return true if game is already started, false otherwise
     */
    fun checkGameStarted(roomId: String, gameId: String): Boolean

    /**
     * finds game in the room by id
     * @param roomId - uuid of the room
     * @param gameId - uuid of the game
     * @return game info response
     */
    fun getGameById(roomId: String, gameId: String): GetGameInfoResponse


}