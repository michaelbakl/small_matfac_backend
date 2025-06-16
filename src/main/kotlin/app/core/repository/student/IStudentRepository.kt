package app.core.repository.student

import app.core.filter.StudentFilter
import app.core.model.person.Student
import app.core.model.person.StudentGroupInfo
import java.time.ZonedDateTime
import java.util.*

interface IStudentRepository {

    /**
     * returns studentId by userId
     * @param userId: id of user
     */
    fun getStudentId(userId: UUID): UUID?

    /**
     * inserts an actual at the moment student into database
     * @param student: object of Person class
     * @return 1 if inserted, 0 otherwise
     */
    fun addActualStudent(student: Student): Int

    /**
     * inserts an old version of student into database
     * @param student: object of Student class
     * @return 1 if inserted, 0 otherwise
     */
    fun addHistoryStudent(student: Student, dateOfChanging: ZonedDateTime): Int

    /**
     * updates a student in database
     * @param person: object of Student class
     * @return 1 if updated, 0 otherwise
     */
    fun updateActualStudent(student: Student): Int

    /**
     * updates a student in database
     * @param student: object of Student class
     * @return 1 if updated, 0 otherwise
     */
    fun updateHistoryStudent(student: Student, dateOfChanging: ZonedDateTime): Int

    /**
     * returns an older version of the student from database by id
     * @param id: id of the version of the student
     * @return 1 if updated, 0 otherwise
     */
    fun getHistoryStudent(id: UUID, dateOfChanging: ZonedDateTime): Student?

    /**
     * returns actual version of student
     * @param id: uuid of person
     * @return 1 if updated, 0 otherwise
     */
    fun getActualStudent(id: UUID): Student?

    /**
     * returns list of students by filter
     * @param filter: filter with params
     * @return list with people
     */
    fun getWithParams(filter: StudentFilter): List<Student>

    /**
     * deletes a student from database
     * @return 1 if deleted, 0 otherwise
     */
    fun deleteActualStudent(id: UUID): Int

    /**
     * deletes a student from database
     * @return 1 if deleted, 0 otherwise
     */
    fun deleteHistoryStudent(id: UUID, dateOfChanging: ZonedDateTime): Int

    /**
     * adds student to the group
     * @param studentGroupId: uuid of the row in the database
     * @param studentId: uuid of the student
     * @param groupId: uuid of the group
     * @param startDate: date of the start being in the group
     * @param endDate: date of the end being in the group (optional, can be null)
     * @param actual: boolean parameter to define if the information is actual
     */
    fun addStudentGroup(
        studentGroupId: UUID,
        studentId: UUID,
        groupId: UUID,
        startDate: ZonedDateTime,
        endDate: ZonedDateTime?,
        actual: Boolean
    ): Int

    /**
     * updates student in the group
     * @param studentGroupId: uuid of the row in the database
     * @param studentId: uuid of the student
     * @param groupId: uuid of the group
     * @param startDate: date of the start being in the group
     * @param endDate: date of the end being in the group (optional, can be null)
     * @param actual: boolean parameter to define if the information is actual
     */
    fun updateStudentGroup(
        studentGroupId: UUID,
        studentId: UUID,
        groupId: UUID,
        startDate: ZonedDateTime,
        endDate: ZonedDateTime?,
        actual: Boolean
    ): Int

    /**
     * returns info about student`s group
     * @param studentGroupId: uuid of the info
     * @return group info
     */
    fun getStudentGroup(studentGroupId: UUID): StudentGroupInfo?

    /**
     * deletes info about student`s group
     * @param studentGroupId: id of the student`s group info
     * @return 1 if deleted, 0 otherwise
     */
    fun deleteStudentGroup(studentGroupId: UUID): Int

    /**
     * adds history about student`s group
     * @param historyId: uuid of the row
     * @param studentGroupId: uuid of the student`s group
     * @param dateOfChanging: date of changing the info
     * @return ?
     */
    fun addHistoryStudentGroup(
        historyId: UUID,
        studentGroupId: UUID,
        dateOfChanging: ZonedDateTime
    ): Int

    /**
     * updates student in the group
     * @param historyId: uuid of the row
     * @param studentGroupId: uuid of the student`s group
     * @param dateOfChanging: date of changing the info
     * @return ?
     */
    fun updateHistoryStudentGroup(
        historyId: UUID,
        studentGroupId: UUID,
        dateOfChanging: ZonedDateTime
    ): Int

    /**
     * returns old info about student`s group
     * @param historyId: uuid of the info
     * @return group info
     */
    fun getHistoryStudentGroup(historyId: UUID): StudentGroupInfo?

    /**
     * deletes an old info about student`s group
     * @param historyId: id of the old student`s group info
     * @return 1 if deleted, 0 otherwise
     */
    fun deleteHistoryStudentGroup(historyId: UUID): Int
}