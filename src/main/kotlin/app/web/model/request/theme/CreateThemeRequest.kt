package ru.baklykov.app.web.model.request.theme

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class CreateThemeRequest(

    @JsonProperty
    val name: String,

    @JsonProperty
    val parentId: UUID? = null
)
