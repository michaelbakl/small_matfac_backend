package ru.baklykov.app.web.model.request.room

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import java.util.*

data class UpdateRoomRequest(

    @NotEmpty(message = "Name cannot be empty or null")
    @JsonProperty
    val name: String,

    @NotEmpty(message = "Id cannot be empty")
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$")
    @JsonProperty
    val teacherId: String,

    @JsonProperty
    val groups: List<UUID>,

    @JsonProperty
    val isClosed: Boolean,

    @JsonProperty
    val participants: List<String>,
)
