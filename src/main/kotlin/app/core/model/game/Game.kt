package ru.baklykov.app.core.model.game

import ru.baklykov.app.core.model.Question
import ru.baklykov.app.core.model.StudentAnswer
import ru.baklykov.app.core.model.StudentResult
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.*

data class Game(
    val gameId: UUID,
    val roomId: UUID,
    val name: String,
    val questions: List<Question>,
    val config: GameConfig,
    val status: String,
    val startDate: ZonedDateTime,
    val finishDate: ZonedDateTime? = startDate.plusMinutes(60),
    val studentsAnswers: List<StudentAnswer>? = listOf(),
    val studentsResults: List<StudentResult>? = listOf(),
)
