package app.web.model.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Ответ с сообщением об успехе или ошибке")
data class ApiMessageResponse(
    @field:Schema(description = "Статус операции", example = "true")
    val success: Boolean,

    @field:Schema(description = "Сообщение от API", example = "Операция выполнена успешно")
    val message: String
)
