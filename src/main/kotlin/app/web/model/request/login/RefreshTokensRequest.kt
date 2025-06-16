package app.web.model.request.login

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema


@Schema(description = "Запрос на обновление access/refresh токенов")
data class RefreshTokensRequest (

    @Schema(description = "Refresh-токен", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    @JsonProperty("refreshToken")
    val refreshToken: String
)
