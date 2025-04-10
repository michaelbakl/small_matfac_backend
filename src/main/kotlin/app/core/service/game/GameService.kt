package ru.baklykov.app.core.service.game

import app.core.exception.NotFoundException
import app.core.exception.RepositoryException
import app.core.exception.ServiceException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.baklykov.app.core.converter.GameConverter
import ru.baklykov.app.core.model.question.Question
import ru.baklykov.app.core.model.game.Game
import ru.baklykov.app.core.model.game.GameConfig
import ru.baklykov.app.core.model.game.GameStatus
import ru.baklykov.app.core.repository.game.IGameRepository
import ru.baklykov.app.core.repository.question.IQuestionRepository
import ru.baklykov.app.web.model.response.game.GetGameInfoResponse
import java.time.ZonedDateTime
import java.util.*

@Service
class GameService(private val gameRepository: IGameRepository, private val questionRepository: IQuestionRepository) :
    IGameService {
    private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)

    override fun startGameInRoom(roomId: String, gameId: String): GetGameInfoResponse {
        try {
            LOGGER.debug("SERVICE start game in the room now {}, {}", roomId, gameId)
            if (checkGameIsInTheRoom(roomId, gameId)) {
                gameRepository.startGame(UUID.fromString(gameId))
                gameRepository.updateGameStatus(UUID.fromString(gameId), GameStatus.CURRENTLY_PLAYED.toString())
            } else {
                throw NotFoundException("Game was not found in the room")
            }
            return getGameById(roomId, gameId)
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error starting game in the room now {}, {}", roomId, gameId, e)
            throw ServiceException("SERVICE start game in the room now exception", e)
        }
    }

    override fun createGame(
        roomId: String,
        name: String,
        gameConfig: GameConfig,
        categories: String
    ): GetGameInfoResponse {
        try {
            LOGGER.debug("SERVICE create game by config {}", gameConfig)
            val questionsToAdd =
                generateQuestions(gameConfig.questions?.let { gameConfig.questionCount?.minus(it.size) }
                    ?: 30, categories)
            gameConfig.questions?.let {
                it.map { item ->
                    {
                        if (questionsToAdd.size < gameConfig.questionCount!!) {
                            questionsToAdd.add(questionRepository.getQuestionById(item))
                        }
                    }
                }
            }

            val game: Game =
                Game(
                    UUID.randomUUID(),
                    UUID.fromString(roomId),
                    name,
                    questionsToAdd,
                    gameConfig,
                    GameStatus.CREATED.toString()
                )

            gameRepository.addGame(game)

            return getGameById(game.roomId.toString(), game.gameId.toString())
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error creating game by config {}", gameConfig, e)
            throw ServiceException("SERVICE create game by config exception", e)
        }
    }

    override fun deleteGameById(gameId: String): Int {
        try {
            LOGGER.debug("SERVICE delete game by id {}", gameId)
            return gameRepository.deleteGameById(UUID.fromString(gameId))
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error deleting game by id {}", gameId, e)
            throw ServiceException("SERVICE delete game by id exception", e)
        }
    }

    override fun addQuestionsToTheGame(roomId: String, gameId: String, questions: List<String>): GetGameInfoResponse {
        try {
            LOGGER.debug("SERVICE add questions to the game {}, {}, {}", roomId, gameId, questions)
            val game = gameRepository.getGameById(UUID.fromString(gameId))
            if (!game.roomId.toString().equals(roomId)) {
                throw NotFoundException("Game was not found in the room")
            }
            questions.let {
                it.map { item ->
                    {
                        // TODO: BAD
                        val question = questionRepository.getQuestionById(UUID.fromString(item))
                        if (!game.questions.contains(question)) {
                            gameRepository.addQuestionToGame(UUID.fromString(gameId), UUID.fromString(item))
                        }
                    }

                }
            }
            return getGameById(roomId, gameId)
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error adding questions to the game {}, {}, {}", roomId, gameId, questions, e)
            throw ServiceException("SERVICE add questions to the game exception", e)
        }
    }

    override fun checkGameStarted(roomId: String, gameId: String): Boolean {
        try {
            LOGGER.debug("SERVICE check game started {} {}", roomId, gameId)
            val game = gameRepository.getGameById(UUID.fromString(gameId))
            if (game.roomId.toString() != roomId) {
                throw NotFoundException("Game is not in the room")
            }
            return game.config.startDate?.isBefore(ZonedDateTime.now(game.config.startDate.zone)) == true
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error checking game started {} {}", roomId, gameId, e)
            throw ServiceException("SERVICE check game started exception", e)
        }
    }

    override fun getGameById(roomId: String, gameId: String): GetGameInfoResponse {
        try {
            LOGGER.debug("SERVICE get game by id {} {}", roomId, gameId)
            val game = gameRepository.getGameById(UUID.fromString(gameId))
            if (game.roomId.toString() != roomId) {
                throw NotFoundException("Game is not in the room")
            }
            return GameConverter.convertToResponse(game)
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error getting game by id {} {}", roomId, gameId, e)
            throw ServiceException("SERVICE get game by id exception", e)
        }
    }

    override fun changeGameDates(
        roomId: String,
        gameId: String,
        startDate: ZonedDateTime?,
        finishDate: ZonedDateTime?
    ): GetGameInfoResponse {
        try {
            LOGGER.debug("SERVICE change game dates {}, {}, {}, {}", roomId, gameId, startDate, finishDate)
            if (checkGameIsInTheRoom(roomId, gameId)) {
                throw NotFoundException("Game is not in the room")
            }
            if (startDate!= null && finishDate != null && !startDate.isBefore(finishDate)) {
                throw ServiceException("Wrong inputs: [start date is not before finish date]")
            }
            val startDateActual = startDate ?: ZonedDateTime.now()
            val finishDateActual = finishDate ?: startDateActual.plusMinutes(30)

            gameRepository.updateGameDates(UUID.fromString(gameId), startDateActual, finishDateActual)
            return getGameById(roomId, gameId)
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error getting game by id {} {}", roomId, gameId, e)
            throw ServiceException("SERVICE get game by id exception", e)
        }
    }

    private fun generateQuestions(num: Int, categories: String): MutableList<Question> {
        try {
            LOGGER.debug("SERVICE private generate questions randomly {} {}", num, categories)
            if (num <= 0) {
                return mutableListOf()
            }
            return questionRepository.getQuestionWithParams(themesStr = categories).toMutableList()
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error private generate questions randomly {} {}", num, categories, e)
            throw ServiceException("SERVICE update group exception", e)
        }
    }

    private fun checkGameIsInTheRoom(roomId: String, gameId: String): Boolean {
        return gameRepository.getGameById(UUID.fromString(gameId)).roomId.toString() == roomId
    }
}
