package ru.baklykov.app.core.converter.teacher

import app.core.converter.ITripleConverter
import app.web.model.request.teacher.AddTeacherRequest
import app.web.model.response.person.GetTeacherInfoResponse
import ru.baklykov.app.core.converter.datetime.ZonedDateConverter
import app.core.model.person.Teacher
import java.util.ArrayList
import java.util.UUID

object TeacherConverter: ITripleConverter<Teacher, AddTeacherRequest, GetTeacherInfoResponse> {

    override fun convertToModel(obj: AddTeacherRequest): Teacher {
        return Teacher(
            obj.teacherId ?: UUID.randomUUID(),
            obj.userId ?: UUID.randomUUID(),
            obj.surname,
            obj.name,
            obj.middleName,
            obj.email,
            obj.dateOfBirth ?.let { ZonedDateConverter.convert(it) },
            null,
        )
    }

    override fun convertToResponse(obj: Teacher): GetTeacherInfoResponse {
        return GetTeacherInfoResponse(
            obj.personId,
            obj.userId,
            obj.surname,
            obj.name,
            obj.middleName,
            obj.email,
            obj.dateOfBirth,
            obj.dateOfEntering
        )
    }

    override fun convertToResponseList(list: List<Teacher>?): List<GetTeacherInfoResponse> {
        val result: MutableList<GetTeacherInfoResponse> = ArrayList()
        list?.let { it.map { item -> result.add(convertToResponse(item)) } }
        return result
    }
}