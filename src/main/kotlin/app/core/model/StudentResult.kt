package app.core.model

import java.util.*

data class StudentResult (
    val studentId: UUID,
    val roomId: UUID,
    val gameId: UUID,
    val results: Map<UUID, Int>, // Map<questionId, points>
    val totalScore: Int
)
