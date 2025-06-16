package ru.baklykov.app.core.filter

import java.time.ZonedDateTime

data class RoomFilter (
    val roomId: String ?= null,
    val teacherId: String ?= null,
    val name: String ?= null,
    val studentIds: List<String>? = listOf(),
    val isClosed: Boolean? = null,
    val firstDate: ZonedDateTime ?= null,
    val secondDate: ZonedDateTime ?= null
)
