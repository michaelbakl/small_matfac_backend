package app.web.model.request.game

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.time.ZonedDateTime

@Schema(description = "Запрос на создание игры")
data class CreateGameRequest(

    @Schema(description = "Список категорий", example = "[\"math\", \"science\"]")
    val categories: List<String>? = listOf(),

    @field:NotBlank
    @Schema(description = "Название игры", example = "Математический турнир", required = true)
    val name: String,

    @field:Min(1)
    @Schema(description = "Количество вопросов", example = "30", defaultValue = "30")
    val questionCount: Int? = 30,

    @field:Min(1)
    @Schema(description = "Продолжительность игры в минутах", example = "30", defaultValue = "30")
    val duration: Int? = 30,

    @Schema(description = "Тип игры (например, 'quiz', 'test')", example = "quiz")
    val gameType: String?,

    @Schema(description = "Уровень сложности (например, 'easy', 'medium', 'hard')", example = "medium")
    val difficulty: String?,

    @Schema(description = "Разрешены ли пропуски вопросов", example = "false", defaultValue = "false")
    val allowSkips: Boolean = false,

    @Schema(description = "Включены ли подсказки", example = "true", defaultValue = "false")
    val enableHints: Boolean = false,

    @Schema(description = "Список ID заранее выбранных вопросов", example = "[\"q1\", \"q2\"]")
    val questions: List<String>? = listOf(),

    @Schema(description = "Дата начала игры", example = "2025-06-08T12:00:00Z", defaultValue = "now")
    val startDate: String? = ZonedDateTime.now().toString(),

    @Schema(description = "Дата окончания игры", example = "2025-06-08T13:00:00Z")
    val finishDate: String? = null
)
