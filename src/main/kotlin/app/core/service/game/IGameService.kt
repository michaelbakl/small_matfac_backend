package app.core.service.game

import app.core.model.game.AnswerResult
import app.web.model.request.game.AnswerSubmissionRequest
import app.core.model.game.GameConfig
import app.web.model.response.game.GetGameInfoResponse
import java.time.ZonedDateTime
import java.util.UUID

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
     * @param creatorId - uuid of the user that creates the question
     * @param roomId - uuid of the room
     * @param name - name of the game
     * @param gameConfig - game configuration
     * @param categories - categories for questions
     * @return game info response
     */
    fun createGame(creatorId: String, roomId: String, name: String, gameConfig: GameConfig, categories: String): GetGameInfoResponse

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
    fun addQuestionsToTheGame(roomId: String, gameId: String, questions: List<String>): GetGameInfoResponse

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

    /**
     * updates game start and finish dates.
     * If start date is null then start date is now, and finish date is 30 minutes from now (if finish date also null).
     * @param roomId - uuid of room
     * @param gameId - uuid of game
     * @param startDate - date of game start
     * @param finishDate - date of game finish
     */
    fun changeGameDates(roomId: String, gameId: String, startDate: ZonedDateTime?, finishDate: ZonedDateTime?): GetGameInfoResponse

    /**
     * Начать игру студентом (создает GameSession, если условий достаточно)
     * @return true, если сессия успешно создана, false если игра недоступна или уже начата
     */
    fun startGameForStudent(gameId: UUID, studentId: UUID): Boolean

    /**
     * Студент отвечает на вопрос
     */
    fun submitAnswer(gameId: UUID, studentId: UUID, answer: AnswerSubmissionRequest): AnswerResult
}
