package app.core.model.person

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZonedDateTime
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
    val startDate: ZonedDateTime,

    @JsonProperty
    val endDate: ZonedDateTime?,

    @JsonProperty
    val actual: Boolean?,

    @JsonProperty
    val dateOfChanging: ZonedDateTime?

)
