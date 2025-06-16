package ru.baklykov.app.core.converter.teacher

import app.core.converter.IConverter
import app.web.model.request.teacher.UpdateTeacherInfoRequest
import ru.baklykov.app.core.converter.datetime.ZonedDateConverter
import app.core.model.person.Teacher
import java.util.*

object TeacherUpdateConverter: IConverter<Teacher, UpdateTeacherInfoRequest> {
    override fun convert(obj: UpdateTeacherInfoRequest): Teacher {
        return Teacher(
            UUID.fromString(obj.teacherId),
            UUID.fromString(obj.userId),
            obj.surname,
            obj.name,
            obj.middleName,
            obj.email,
            if (obj.dateOfBirth != null) ZonedDateConverter.convert(obj.dateOfBirth) else null,
            null
        )
    }
}