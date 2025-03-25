package app.core.converter

import app.web.model.request.student.AddStudentRequest
import app.web.model.response.person.GetStudentInfoResponse
import ru.baklykov.app.core.model.person.Student
import java.util.*

object StudentConverter: ITripleConverter<Student, AddStudentRequest, GetStudentInfoResponse> {
    override fun convertToModel(obj: AddStudentRequest): Student {
        return Student(
            obj.studentId ?: UUID.randomUUID(),
            obj.userId ?: UUID.randomUUID(),
            obj.surname,
            obj.name,
            obj.middleName,
            obj.email,
            obj.dateOfBirth ?.let { LocalDateConverter.convert(it) },
            obj.dateOfEntering ?.let { LocalDateConverter.convert(it) },
            obj.groups
        )
    }

    override fun convertToResponse(obj: Student): GetStudentInfoResponse {
        return GetStudentInfoResponse(
            obj.personId,
            obj.userId,
            obj.surname,
            obj.name,
            obj.middleName,
            obj.email,
            obj.dateOfBirth,
            obj.dateOfEntering,
            obj.groups
        )
    }

    override fun convertToResponseList(list: List<Student>?): List<GetStudentInfoResponse> {
        val result: MutableList<GetStudentInfoResponse> = ArrayList()
        list?.let { it.map { item -> result.add(convertToResponse(item)) } }
        return result
    }
}