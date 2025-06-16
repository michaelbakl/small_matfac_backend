package app.web.model.response.question

import app.web.model.response.answer.AnswerResponse
import io.swagger.v3.oas.annotations.media.Schema
import app.web.model.response.theme.ThemeResponse
import java.util.*

@Schema(description = "Информация о вопросе")
data class GetQuestionInfoResponse(

    @field:Schema(description = "Уникальный идентификатор вопроса", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    val questionId: UUID,

    @field:Schema(description = "Идентификатор владельца вопроса", example = "7fa85f64-5717-4562-b3fc-2c963f66afa7")
    val ownerId: UUID,

    @field:Schema(description = "Заголовок вопроса", example = "Что такое JVM?")
    val title: String,

    @field:Schema(description = "Список тем, связанных с вопросом")
    val themes: List<ThemeResponse>,

    @field:Schema(description = "Список идентификаторов картинок, связанных с вопросом")
    val pictures: List<UUID>,

    @field:Schema(description = "Описание вопроса", example = "Краткое объяснение о JVM")
    val description: String,

    @field:Schema(description = "Список вариантов ответов")
    val answers: List<AnswerResponse>
)
