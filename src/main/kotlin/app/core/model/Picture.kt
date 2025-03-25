package ru.baklykov.app.core.model

import java.time.LocalDateTime
import java.util.*

data class Picture(
    val pictureId: UUID,
    val picture: String,
    val dateOfSaving: LocalDateTime
)
