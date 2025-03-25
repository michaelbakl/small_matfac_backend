package ru.baklykov.app.core.model

import java.util.*

data class StudentResult (
    val studentId: UUID,
    val roomId: UUID,
    val results: Map<UUID, Int>, // Map<questionId, points>
    val totalScore: Int
)
