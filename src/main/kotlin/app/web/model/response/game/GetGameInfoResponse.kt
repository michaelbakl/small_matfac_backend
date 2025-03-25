package ru.baklykov.app.web.model.response.game

import ru.baklykov.app.core.model.StudentAnswer
import ru.baklykov.app.core.model.StudentResult
import ru.baklykov.app.core.model.game.DifficultyLevel
import ru.baklykov.app.core.model.game.GameType
import ru.baklykov.app.web.model.response.question.GetQuestionInfoResponse
import java.time.LocalDateTime
import java.util.*

data class GetGameInfoResponse(
    val gameId: UUID,
    val roomId: UUID,
    val questions: List<GetQuestionInfoResponse>,
    val categories: List<UUID>,
    val questionCount: Int,
    val duration: Int,
    val gameType: GameType,
    val difficulty: DifficultyLevel,
    val allowSkips: Boolean,
    val enableHints: Boolean,
    val status: String,
    val startDate: LocalDateTime,
    val finishDate: LocalDateTime?,
    val studentsAnswers: List<StudentAnswer> ?= listOf(),
    val studentsResults: List<StudentResult> ?= listOf(),
)
