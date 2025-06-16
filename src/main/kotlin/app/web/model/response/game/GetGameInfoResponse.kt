package app.web.model.response.game

import app.core.model.StudentAnswer
import app.core.model.StudentResult
import app.core.model.game.DifficultyLevel
import app.core.model.game.GameType
import io.swagger.v3.oas.annotations.media.Schema
import app.web.model.response.question.GetQuestionInfoResponse
import java.time.ZonedDateTime
import java.util.*

@Schema(description = "Информация об игре")
data class GetGameInfoResponse(
    @field:Schema(description = "Уникальный идентификатор игры", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    val gameId: UUID,

    @field:Schema(description = "Уникальный идентификатор комнаты", example = "7fa85f64-5717-4562-b3fc-2c963f66afa7")
    val roomId: UUID,

    @field:Schema(description = "Название игры", example = "Викторина по математике")
    val name: String,

    @field:Schema(description = "Список вопросов")
    val questions: List<GetQuestionInfoResponse>,

    @field:Schema(description = "Количество вопросов", example = "10")
    val questionCount: Int,

    @field:Schema(description = "Продолжительность игры в минутах", example = "60")
    val duration: Int,

    @field:Schema(description = "Тип игры")
    val gameType: GameType,

    @field:Schema(description = "Уровень сложности игры")
    val difficulty: DifficultyLevel,

    @field:Schema(description = "Разрешены ли пропуски вопросов", example = "true")
    val allowSkips: Boolean,

    @field:Schema(description = "Включены ли подсказки", example = "false")
    val enableHints: Boolean,

    @field:Schema(description = "Статус игры", example = "ACTIVE")
    val status: String,

    @field:Schema(description = "Дата и время начала игры", example = "2025-06-09T15:00:00Z")
    val startDate: ZonedDateTime,

    @field:Schema(description = "Дата и время окончания игры", example = "2025-06-09T16:00:00Z", nullable = true)
    val finishDate: ZonedDateTime?,

    @field:Schema(description = "Ответы студентов", nullable = true)
    val studentsAnswers: List<StudentAnswer>? = listOf(),

    @field:Schema(description = "Результаты студентов", nullable = true)
    val studentsResults: List<StudentResult>? = listOf(),
)
