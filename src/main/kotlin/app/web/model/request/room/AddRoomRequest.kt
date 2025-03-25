package ru.baklykov.app.web.model.request.room

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern

data class AddRoomRequest (

    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$")
    @JsonProperty
    var teacherId: String,

    @NotEmpty(message = "Name can not be empty")
    @JsonProperty
    val name: String,

    @JsonProperty
    val students: List<String>,

    @JsonProperty
    val isClosed: Boolean? = false,
)
