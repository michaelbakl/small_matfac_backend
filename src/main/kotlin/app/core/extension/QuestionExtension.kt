package app.core.extension

import app.core.model.question.Question
import ru.baklykov.app.core.repository.question.IQuestionThemeRepository
import app.web.model.response.answer.AnswerResponse
import app.web.model.response.question.GetQuestionInfoResponse
import app.web.model.response.theme.ThemeResponse
import java.util.*


fun Question.toResponse(themeRepository: IQuestionThemeRepository): GetQuestionInfoResponse {
    return GetQuestionInfoResponse(
        questionId = questionId,
        title = title,
        description = description,
        pictures = pictures,
        answers = answers.map { (id, text, correct, points) ->
            AnswerResponse(
                id = id ?: UUID.randomUUID(),
                text = text,
                correct = correct,
                points = points
            )
        },
        themes = themeRepository.findByQuestionId(questionId).map { (id, path, name, level) ->
            ThemeResponse(
                id = id,
                name = name,
                path = path,
                level = level,
                parentId = themeRepository.getParentThemes(id).last().id,
                hasChildren = themeRepository.findChildThemes(id).isNotEmpty()
            )
        },
        ownerId = ownerId
    )
}