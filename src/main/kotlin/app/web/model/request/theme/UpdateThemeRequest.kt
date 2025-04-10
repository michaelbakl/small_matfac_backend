package ru.baklykov.app.web.model.request.theme

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateThemeRequest(

    @JsonProperty
    val name: String
)

