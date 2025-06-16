package app.core.repository.subject

import app.core.filter.SubjectFilter
import app.core.model.Subject
import java.util.*

interface ISubjectRepository {
    fun addSubject(subject: Subject): Int
    fun updateSubject(subject: Subject): Int
    fun delete(id: UUID): Int
    fun getById(id: UUID): Subject?
    fun getByFilter(filter: SubjectFilter): List<Subject>
    fun getAll(): List<Subject>
}
