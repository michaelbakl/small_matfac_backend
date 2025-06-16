package ru.baklykov.app.core.exception.theme

import ru.baklykov.app.core.exception.question.QuestionException
import java.util.*

class ThemeNotFoundException(themeId: UUID) :
    QuestionException("Theme with id $themeId not found")