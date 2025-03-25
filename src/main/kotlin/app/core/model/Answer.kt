package ru.baklykov.app.core.model

import java.util.*

data class Answer (
    val answerId: UUID,
    val description: String,
    val pictures: List<Picture>
)
