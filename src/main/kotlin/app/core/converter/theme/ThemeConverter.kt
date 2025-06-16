package ru.baklykov.app.core.converter.theme

import app.core.converter.IConverter
import app.core.model.question.QuestionTheme
import app.web.model.response.theme.ThemeResponse

object ThemeConverter: IConverter<ThemeResponse, QuestionTheme> {
    override fun convert(obj: QuestionTheme): ThemeResponse {
        return ThemeResponse(
            obj.id, obj.name, obj.path, obj.level, hasChildren = true,
            parentId = TODO()
        )
    }
}