package app.core.repository.teacher

import app.core.filter.TeacherFilter
import app.core.model.person.Teacher
import java.time.ZonedDateTime
import java.util.*

interface ITeacherRepository {

    /**
     * returns teacherId by userId
     * @param userId: id of user
     */
    fun getTeacherId(userId: UUID): UUID?

    /**
     * inserts an actual at the moment teacher into database
     * @param teacher: object of Teacher class
     * @return 1 if inserted, 0 otherwise
     */
    fun addActualTeacher(teacher: Teacher): Int

    /**
     * inserts an old version of teacher into database
     * @param teacher: object of Teacher class
     * @return 1 if inserted, 0 otherwise
     */
    fun addHistoryTeacher(teacher: Teacher, dateOfChanging: ZonedDateTime): Int

    /**
     * updates a teacher in database
     * @param teacher: object of Teacher class
     * @return 1 if updated, 0 otherwise
     */
    fun updateActualTeacher(teacher: Teacher): Int

    /**
     * updates a teacher in database
     * @param teacher: object of Teacher class
     * @return 1 if updated, 0 otherwise
     */
    fun updateHistoryTeacher(teacher: Teacher, dateOfChanging: ZonedDateTime): Int

    /**
     * returns a version of Teacher from database if exists
     * @param id: id of the version of teacher
     * @return teacher or null
     */
    fun getHistoryTeacher(id: UUID, dateOfChanging: ZonedDateTime): Teacher?

    /**
     * returns actual version of teacher
     * @param id: uuid of teacher
     * @return teacher or null
     */
    fun getActualTeacher(id: UUID): Teacher?

    /**
     * returns list of teachers by filter
     * @param filter: filter with params
     * @return list with teachers
     */
    fun getWithParams(filter: TeacherFilter): List<Teacher>

    /**
     * deletes a teacher from database
     * @return 1 if deleted, 0 otherwise
     */
    fun deleteActualTeacher(id: UUID): Int

    /**
     * deletes a teacher from database
     * @return 1 if deleted, 0 otherwise
     */
    fun deleteHistoryTeacher(id: UUID, dateOfChanging: ZonedDateTime): Int

    fun getAllActualTeachers(): List<Teacher>
}