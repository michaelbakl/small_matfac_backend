package ru.baklykov.app.core.exception.question

import java.util.*

class QuestionCreationException(msg: String, e: Exception? = null) :
    RuntimeException("Question cannot be created", e)