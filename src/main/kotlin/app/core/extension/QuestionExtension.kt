package ru.baklykov.app.core.extension

import ru.baklykov.app.core.model.question.Question
import ru.baklykov.app.core.repository.question.IQuestionThemeRepository
import ru.baklykov.app.web.model.response.answer.AnswerResponse
import ru.baklykov.app.web.model.response.question.GetQuestionInfoResponse


fun Question.toResponse(themeRepository: IQuestionThemeRepository): GetQuestionInfoResponse {
    return GetQuestionInfoResponse(
        questionId = questionId,
        title = title,
        description = description,
        pictures = pictures,
        answers = answers.map { (answer, correctness) ->
            AnswerResponse(
                id = answer,
                text = answer.toString(),
                correct = correctness.first,
                points = correctness.second
            )
        },
        themes = themeRepository.findByQuestionId(questionId),
        createdAt = createdAt
    )
}