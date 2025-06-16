package ru.baklykov.app.core.converter.question

import app.core.converter.IConverter
import app.core.exception.ConverterException
import app.web.model.dto.answer.AnswerDto
import app.web.model.response.answer.AnswerResponse

object AnswerConverter : IConverter<AnswerResponse, AnswerDto> {

    override fun convert(obj: AnswerDto): AnswerResponse {
        return AnswerResponse(
            obj.id ?: throw ConverterException(error = "Unable to convert answer"),
            obj.text,
            obj.correct,
            obj.points
        )
    }
}