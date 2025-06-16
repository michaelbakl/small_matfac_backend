package app.core.converter.question

import app.core.converter.ITripleConverter
import ru.baklykov.app.core.converter.theme.ThemeConverter
import app.core.model.question.Question
import app.web.model.request.question.AddQuestionRequest
import ru.baklykov.app.core.converter.question.AnswerConverter
import app.web.model.response.question.GetQuestionInfoResponse
import java.util.ArrayList

object QuestionConverter: ITripleConverter<Question, AddQuestionRequest, GetQuestionInfoResponse> {
    override fun convertToModel(obj: AddQuestionRequest): Question {
        TODO("Not yet implemented")
    }

    override fun convertToResponseList(list: List<Question>?): List<GetQuestionInfoResponse> {
        val result: MutableList<GetQuestionInfoResponse> = ArrayList()
        list?.let { it.map { item -> result.add(convertToResponse(item)) } }
        return result
    }

    override fun convertToResponse(obj: Question): GetQuestionInfoResponse {
        return GetQuestionInfoResponse(
            obj.questionId,
            obj.ownerId,
            obj.title,
            obj.themes.map { ThemeConverter.convert(it) },
            obj.pictures,
            obj.description,
            obj.answers.map { AnswerConverter.convert(it) }
        )
    }
}