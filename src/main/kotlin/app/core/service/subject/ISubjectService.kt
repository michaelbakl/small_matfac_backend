package app.core.service.subject

import app.web.model.request.subject.GetSubjectsWithParamsRequest
import app.web.model.response.subject.GetSubjectsResponse
import app.web.model.response.subject.GetSubjectResponse
import app.core.model.Subject

interface ISubjectService {
    fun addSubject(request: Subject): GetSubjectResponse
    fun updateSubject(request: Subject): GetSubjectResponse
    fun deleteSubjectById(id: String): GetSubjectResponse
    fun getSubjectById(id: String): GetSubjectResponse
    fun getSubjectsByFilter(request: GetSubjectsWithParamsRequest): GetSubjectsResponse
    fun getAll(): GetSubjectsResponse
}