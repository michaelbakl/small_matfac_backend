package app.core.service.game

import app.core.exception.NotFoundException
import app.core.exception.RepositoryException
import app.core.exception.ServiceException
import app.core.model.game.AnswerResult
import app.core.model.game.AnswerSubmission
import app.core.model.game.GameSession
import app.core.repository.answer.IAnswerSubmissionRepository
import app.core.repository.game.IGameSessionRepository
import app.web.model.request.game.AnswerSubmissionRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import app.core.converter.game.GameConverter
import app.core.model.question.Question
import app.core.model.game.Game
import app.core.model.game.GameConfig
import ru.baklykov.app.core.model.game.GameStatus
import app.core.repository.game.IGameRepository
import ru.baklykov.app.core.repository.question.IQuestionRepository
import ru.baklykov.app.core.repository.room.IRoomRepository
import app.web.model.response.game.GetGameInfoResponse
import java.time.ZonedDateTime
import java.util.*

@Service
open class GameService(
    private val gameRepository: IGameRepository,
    private val questionRepository: IQuestionRepository,
    private val sessionRepository: IGameSessionRepository,
    private val submissionRepository: IAnswerSubmissionRepository,
    private val roomRepository: IRoomRepository
) :
    IGameService {
    private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)

    @Transactional(rollbackFor = [RepositoryException::class, Exception::class])
    override fun startGameInRoom(roomId: String, gameId: String): GetGameInfoResponse {
        try {
            LOGGER.debug("SERVICE start game in the room now {}, {}", roomId, gameId)
            val startDate: ZonedDateTime = ZonedDateTime.now()
            if (checkGameIsInTheRoom(roomId, gameId)) {
                val gameUUID = UUID.fromString(gameId)
                val game: Game = gameRepository.getGameById(gameUUID)
                val finishDate: ZonedDateTime = if (game.config.finishDate?.isBefore(startDate) == true) {
                    ZonedDateTime.now().plusMinutes(30)
                } else {
                    game.config.finishDate ?: startDate.plusMinutes(30)
                }
                gameRepository.updateGameDates(gameUUID, startDate, finishDate)
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

    @Transactional(rollbackFor = [RepositoryException::class, Exception::class])
    override fun createGame(
        creatorId: String,
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
                            questionRepository.getQuestionById(item)?.let { it1 -> questionsToAdd.add(it1) }
                        }
                    }
                }
            }

            val game: Game =
                Game(
                    UUID.randomUUID(),
                    UUID.fromString(roomId),
                    UUID.fromString(creatorId),
                    questions = questionsToAdd,
                    gameConfig,
                    GameStatus.CREATED.toString()
                )

            gameRepository.createGame(game)

            return getGameById(game.roomId.toString(), game.gameId.toString())
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error creating game by config {}", gameConfig, e)
            throw ServiceException("SERVICE create game by config exception", e)
        }
    }

    @Transactional(rollbackFor = [RepositoryException::class, Exception::class])
    override fun deleteGameById(gameId: String): Int {
        try {
            LOGGER.debug("SERVICE delete game by id {}", gameId)
            return gameRepository.deleteGameById(UUID.fromString(gameId))
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error deleting game by id {}", gameId, e)
            throw ServiceException("SERVICE delete game by id exception", e)
        }
    }

    @Transactional(rollbackFor = [RepositoryException::class, Exception::class])
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
                        gameRepository.addQuestionToGame(UUID.fromString(gameId), UUID.fromString(item))
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

    @Transactional(rollbackFor = [RepositoryException::class, Exception::class])
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
            if (startDate != null && finishDate != null && !startDate.isBefore(finishDate)) {
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

    @Transactional(rollbackFor = [RepositoryException::class, Exception::class])
    override fun startGameForStudent(gameId: UUID, studentId: UUID): Boolean {
        LOGGER.debug("SERVICE start game for student {}, {}", gameId, studentId)
        try {
            val game = gameRepository.getGameById(gameId)

            // проверка, что игра в статусе CREATED или CURRENTLY_PLAYED и в пределах времени
            val now = ZonedDateTime.now()
            if (game.status != GameStatus.CREATED.toString() && game.status != GameStatus.CURRENTLY_PLAYED.toString()) return false
            if (game.config.startDate?.isAfter(now) == true || game.config.finishDate?.isBefore(now) == true) return false

            // проверка, находится ли студент в комнате
            val roomId = game.roomId
            val studentInRoom = roomRepository.isStudentInRoom(roomId, studentId)
            if (!studentInRoom) return false

            // проверка, не начата ли уже игровая сессия
            val existingSession = sessionRepository.findByGameIdAndStudentId(gameId, studentId)
            if (existingSession != null) return false

            // создаем новую сессию
            val session = GameSession(
                sessionId = UUID.randomUUID(),
                gameId = gameId,
                studentId = studentId,
                startedAt = now,
                currentQuestionIndex = 0
            )

            return sessionRepository.createSession(session) > 0
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error starting game for student {} {}", gameId, studentId, e)
            throw ServiceException("SERVICE start game for student exception", e)
        }
    }

    @Transactional(rollbackFor = [RepositoryException::class, Exception::class, NotFoundException::class])
    override fun submitAnswer(gameId: UUID, studentId: UUID, answer: AnswerSubmissionRequest): AnswerResult {
        LOGGER.debug("SERVICE submit answer for student {}, {}, {}", gameId, studentId, answer)
        try {
            val game = gameRepository.getGameById(gameId)
            val session = sessionRepository.findByGameIdAndStudentId(gameId, studentId)
                ?: throw IllegalStateException("Game not started for this student")

            if (game.status != GameStatus.CURRENTLY_PLAYED.toString()) {
                throw IllegalStateException("Game is not active")
            }

            val now = ZonedDateTime.now()
            if (now.isBefore(game.config.startDate) || now.isAfter(game.config.finishDate)) {
                throw IllegalStateException("Game time expired")
            }

            val question = game.questions[session.currentQuestionIndex]
            if (question.questionId != answer.questionId)
                throw IllegalArgumentException("Unexpected question")

            val correctAnswer = question.answers.firstOrNull { it.correct }
            val isCorrect = when {
                answer.selectedOptionId != null -> correctAnswer?.id == answer.selectedOptionId
                answer.typedAnswer != null -> correctAnswer?.text?.equals(
                    answer.typedAnswer,
                    ignoreCase = true
                ) == true

                else -> false
            }

            submissionRepository.save(
                AnswerSubmission(
                    submissionId = UUID.randomUUID(),
                    sessionId = session.sessionId,
                    questionId = answer.questionId,
                    selectedOptionId = answer.selectedOptionId,
                    typedAnswer = answer.typedAnswer,
                    isCorrect = isCorrect,
                    submittedAt = now
                )
            )

            val nextIndex = session.currentQuestionIndex + 1
            val gameFinished = nextIndex >= game.questions.size

            sessionRepository.updateSession(session.copy(currentQuestionIndex = nextIndex))

            return AnswerResult(
                isCorrect = isCorrect,
                nextQuestionId = game.questions.getOrNull(nextIndex)?.questionId,
                gameFinished = gameFinished
            )
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error submitting answer for student {} {} {}", gameId, studentId, answer, e)
            throw ServiceException("SERVICE submit answer for student exception", e)
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
