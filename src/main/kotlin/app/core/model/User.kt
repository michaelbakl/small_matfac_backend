package ru.baklykov.app.core.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class User(

    @JsonProperty("userId")
    val userId: UUID,

    @JsonProperty("email")
    val email: String,

    @JsonProperty("roles")
    val roles: List<String>,

    @JsonIgnore
    val password: String? = ""
)
