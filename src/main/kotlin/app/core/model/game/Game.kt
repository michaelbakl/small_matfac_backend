package ru.baklykov.app.core.model.game

import ru.baklykov.app.core.model.question.Question
import ru.baklykov.app.core.model.StudentAnswer
import ru.baklykov.app.core.model.StudentResult
import java.util.*

data class Game(
    val gameId: UUID,
    val roomId: UUID,
    val creatorId: UUID,
    val questions: List<Question>,
    val config: GameConfig,
    val status: String,
    val studentsAnswers: List<StudentAnswer>? = listOf(),
    val studentsResults: List<StudentResult>? = listOf(),
)
