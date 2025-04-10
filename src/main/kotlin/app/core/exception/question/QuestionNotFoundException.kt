package ru.baklykov.app.core.exception.question

import java.util.*

class QuestionNotFoundException(questionId: UUID) :
    RuntimeException("Question with id $questionId not found")