package app.core.service.game

import app.core.model.game.Game
import app.core.model.game.GameConfig
import app.core.model.game.GameSession
import app.core.repository.answer.IAnswerSubmissionRepository
import app.core.repository.game.IGameRepository
import app.core.repository.game.IGameSessionRepository
import app.web.model.request.game.AnswerSubmissionRequest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import ru.baklykov.app.core.model.game.GameStatus
import app.core.model.question.Question
import ru.baklykov.app.core.repository.question.IQuestionRepository
import ru.baklykov.app.core.repository.room.IRoomRepository
import app.web.model.dto.answer.AnswerDto
import java.time.ZonedDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GameServiceTest {

    @Mock
    lateinit var gameRepository: IGameRepository

    @Mock
    lateinit var questionRepository: IQuestionRepository

    @Mock
    lateinit var sessionRepository: IGameSessionRepository

    @Mock
    lateinit var submissionRepository: IAnswerSubmissionRepository

    @Mock
    lateinit var roomRepository: IRoomRepository

    lateinit var gameService: GameService

    @BeforeEach
    fun setup() {
        gameService = GameService(
            gameRepository,
            questionRepository,
            sessionRepository,
            submissionRepository,
            roomRepository
        )
    }

    @Test
    fun `startGameInRoom should update game status and return info`() {
        // given
        val roomId = UUID.randomUUID().toString()
        val gameId = UUID.randomUUID().toString()
        val game = Game(
            UUID.fromString(gameId),
            UUID.fromString(roomId),
            UUID.randomUUID(),
            listOf(),
            GameConfig(startDate = ZonedDateTime.now().minusMinutes(1)),
            GameStatus.CREATED.toString()
        )

        whenever(gameRepository.getGameById(UUID.fromString(gameId))).thenReturn(game)
        whenever(gameRepository.updateGameDates(any(), any(), any())).thenReturn(1)
        whenever(gameRepository.updateGameStatus(any(), any())).thenReturn(1)
        whenever(gameRepository.getGameById(UUID.fromString(gameId))).thenReturn(game)

        // when
        val result = gameService.startGameInRoom(roomId, gameId)

        // then
        assertEquals(roomId, result.roomId.toString())
        verify(gameRepository).updateGameStatus(UUID.fromString(gameId), GameStatus.CURRENTLY_PLAYED.toString())
    }

    @Test
    fun `createGame should save game and return it`() {
        val creatorId = UUID.randomUUID().toString()
        val roomId = UUID.randomUUID().toString()
        val gameConfig = GameConfig(
            questionCount = 1,
            questions = listOf(),
            startDate = ZonedDateTime.now(),
            finishDate = ZonedDateTime.now().plusMinutes(10)
        )
        val question = mock<Question>()

        whenever(
            questionRepository.getQuestionWithParams(
                questionId = anyOrNull(),
                ownerId = anyOrNull(),
                description = anyOrNull(),
                title = any(),
                themes = any(),
                themesStr = eq("math")
            )
        ).thenReturn(mutableListOf(question))
        whenever(gameRepository.createGame(any())).thenReturn(1)
        whenever(gameRepository.getGameById(any())).thenAnswer { invocation ->
            val id = invocation.arguments[0] as UUID
            Game(
                id,
                UUID.fromString(roomId),
                UUID.fromString(creatorId),
                listOf(question),
                gameConfig,
                GameStatus.CREATED.toString()
            )
        }

        val response = gameService.createGame(creatorId, roomId, "Test Game", gameConfig, "math")
        assertEquals("Test Game", response.name)
    }

    @Test
    fun `startGameForStudent should start session if conditions met`() {
        val gameId = UUID.randomUUID()
        val studentId = UUID.randomUUID()
        val roomId = UUID.randomUUID()

        val game = Game(
            gameId,
            roomId,
            UUID.randomUUID(),
            questions = listOf(mock()),
            config = GameConfig(
                startDate = ZonedDateTime.now().minusMinutes(1),
                finishDate = ZonedDateTime.now().plusMinutes(30)
            ),
            status = GameStatus.CREATED.toString()
        )

        whenever(gameRepository.getGameById(gameId)).thenReturn(game)
        whenever(roomRepository.isStudentInRoom(roomId, studentId)).thenReturn(true)
        whenever(sessionRepository.findByGameIdAndStudentId(gameId, studentId)).thenReturn(null)
        whenever(sessionRepository.createSession(any())).thenReturn(1)

        val result = gameService.startGameForStudent(gameId, studentId)

        assertTrue(result)
    }

    @Test
    fun `submitAnswer should return correct AnswerResult`() {
        val ownerId = UUID.randomUUID()
        val gameId = UUID.randomUUID()
        val studentId = UUID.randomUUID()
        val sessionId = UUID.randomUUID()
        val questionId = UUID.randomUUID()
        val answerId = UUID.randomUUID()

        val question = Question(
            questionId = questionId,
            ownerId = ownerId,
            title = "text",
            type = "SINGLE",
            pictures = emptyList(),
            description = "Description",
            answers = listOf(
                AnswerDto(UUID.randomUUID(), "wrong", false, 0),
                AnswerDto(answerId, "right", true, 1)
            )
        )

        val game = Game(
            gameId,
            UUID.randomUUID(),
            UUID.randomUUID(),
            questions = listOf(question),
            config = GameConfig(
                startDate = ZonedDateTime.now().minusMinutes(1),
                finishDate = ZonedDateTime.now().plusMinutes(10)
            ),
            status = GameStatus.CURRENTLY_PLAYED.toString()
        )

        val session = GameSession(sessionId, gameId, studentId, ZonedDateTime.now(), currentQuestionIndex = 0)

        whenever(gameRepository.getGameById(gameId)).thenReturn(game)
        whenever(sessionRepository.findByGameIdAndStudentId(gameId, studentId)).thenReturn(session)
        whenever(submissionRepository.save(any())).thenReturn(1)
        whenever(sessionRepository.updateSession(any())).thenReturn(1)

        val request = AnswerSubmissionRequest(questionId, selectedOptionId = answerId)

        val result = gameService.submitAnswer(gameId, studentId, request)

        assertTrue(result.isCorrect)
        assertTrue(result.gameFinished)
        assertNull(result.nextQuestionId)
    }
}
