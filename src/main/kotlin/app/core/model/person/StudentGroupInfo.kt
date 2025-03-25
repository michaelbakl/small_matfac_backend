package ru.baklykov.app.core.model.person

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.util.UUID

data class StudentGroupInfo (
    @JsonProperty
    val studentGroupId: UUID,

    @JsonProperty
    val studentId: UUID,

    @JsonProperty
    val groupId: UUID,

    @JsonProperty
    val groupName: String,

    @JsonProperty
    val startDate: LocalDateTime,

    @JsonProperty
    val endDate: LocalDateTime?,

    @JsonProperty
    val actual: Boolean?,

    @JsonProperty
    val dateOfChanging: LocalDateTime?

)
