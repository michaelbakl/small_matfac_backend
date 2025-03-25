package app.core.converter

import app.web.model.request.teacher.UpdateTeacherInfoRequest
import ru.baklykov.app.core.model.person.Teacher
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
            if (obj.dateOfBirth != null) LocalDateConverter.convert(obj.dateOfBirth) else null,
            null
        )
    }
}