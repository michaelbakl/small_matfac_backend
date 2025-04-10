package ru.baklykov.app.core.exception.question

import java.util.*

class QuestionAccessDeniedException(questionId: UUID) :
    RuntimeException("Access to question $questionId denied")