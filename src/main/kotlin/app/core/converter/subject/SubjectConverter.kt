package ru.baklykov.app.core.converter.subject

import app.core.converter.IConverter
import app.web.model.response.subject.GetSubjectResponse
import app.core.model.Subject

object SubjectConverter: IConverter<GetSubjectResponse, Subject> {
    override fun convert(obj: Subject): GetSubjectResponse {
        return GetSubjectResponse(obj.id.toString(), obj.name)
    }

}