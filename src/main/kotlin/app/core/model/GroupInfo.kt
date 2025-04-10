package ru.baklykov.app.core.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZonedDateTime
import java.util.*

data class GroupInfo (

    @JsonProperty
    val groupId: UUID,

    @JsonProperty
    val name: String,

    @JsonProperty
    val dateOfCreating: ZonedDateTime,

    @JsonProperty
    val teacherId: UUID,

    @JsonProperty
    val assistants: List<UUID>,

    @JsonProperty
    val classNum: Int

)
