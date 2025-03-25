package ru.baklykov.app.core.filter

import java.time.ZonedDateTime
import java.util.*

data class RoomFilter (
    val roomId: String?,
    val teacherId: String?,
    val name: String?,
    val firstDate: ZonedDateTime?,
    val secondDate: ZonedDateTime?
)
