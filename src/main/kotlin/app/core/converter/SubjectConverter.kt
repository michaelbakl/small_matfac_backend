package app.core.converter

import app.web.model.response.subject.GetSubjectResponse
import ru.baklykov.app.core.model.Subject

object SubjectConverter: IConverter<GetSubjectResponse, Subject> {
    override fun convert(obj: Subject): GetSubjectResponse {
        return GetSubjectResponse(obj.id.toString(), obj.name)
    }

}