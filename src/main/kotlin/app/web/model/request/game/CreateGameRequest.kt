package ru.baklykov.app.web.model.request.game

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZonedDateTime

data class CreateGameRequest(

    @JsonProperty
    val categories: String = "",

    @JsonProperty
    val name: String,

    @JsonProperty
    val questionCount: Int ?= 30,

    @JsonProperty
    val duration: Int ?= 30,

    @JsonProperty
    val gameType: String?,

    @JsonProperty
    val difficulty: String?,

    @JsonProperty
    val allowSkips: Boolean = false,

    @JsonProperty
    val enableHints: Boolean = false,

    @JsonProperty
    val questions: List<String> ?= listOf(),

    @JsonProperty
    val startDate: String ?= ZonedDateTime.now().toString()

)
