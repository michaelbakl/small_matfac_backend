package ru.baklykov.app.web.model.request.game

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateDatesInGameRequest(

    @JsonProperty("startDate")
    val startDate: String = "",

    @JsonProperty("finishDate")
    val finishDate: String = ""
)
