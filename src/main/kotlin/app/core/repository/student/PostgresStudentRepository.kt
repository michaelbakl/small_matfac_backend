package app.core.repository.student

import app.core.exception.RepositoryException
import app.core.filter.StudentFilter
import app.core.util.SqlQueryBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.baklykov.app.core.converter.datetime.TimestampzConverter
import app.core.model.person.Student
import app.core.model.person.StudentGroupInfo
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.ZonedDateTime
import java.util.*

@Repository
open class PostgresStudentRepository(private val jdbcOperations: JdbcOperations) : IStudentRepository {
    private val LOGGER: Logger = LoggerFactory.getLogger(PostgresStudentRepository::class.java)

    private val rowMapper = { resultSet: ResultSet, i: Int ->
        Student(
            UUID.fromString(resultSet.getString("studentId")),
            UUID.fromString(resultSet.getString("userId")),
            resultSet.getString("surname"),
            resultSet.getString("name"),
            resultSet.getString("middleName"),
            resultSet.getString("email"),
            TimestampzConverter.convert(resultSet.getTimestamp("dateOfBirth")),
            TimestampzConverter.convert(resultSet.getTimestamp("dateOfEntering"))
        )
    }

    override fun getStudentId(userId: UUID): UUID? {
        LOGGER.debug("REPOSITORY get student id by user id {}", userId)
        try {
            val sql = "SELECT studentId FROM student WHERE userId = ?"
            LOGGER.debug("REPOSITORY get student id by user id: {} sql: {}", userId, sql)
            return jdbcOperations.queryForObject(sql, { resultSet: ResultSet, i: Int ->
                UUID.fromString(resultSet.getString("studentId"))
            }, userId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t get student id by user id {}", userId)
            throw RepositoryException("REPOSITORY Can`t get student id exception", e)
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun addActualStudent(student: Student): Int {
        LOGGER.debug("REPOSITORY add actual student {}", student)
        try {
            val sql =
                "INSERT INTO student (studentId, userId, surname, name, middleName, email, dateOfBirth, dateOfEntering)" +
                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            LOGGER.debug("REPOSITORY add student params: {} sql: {}", student, sql)
            return jdbcOperations.update(
                sql,
                student.personId,
                student.userId,
                student.surname,
                student.name,
                student.middleName,
                student.email,
                TimestampzConverter.convertFrom(student.dateOfBirth),
                TimestampzConverter.convertFrom(student.dateOfEntering)
            )
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t add actual student {}", student)
            throw RepositoryException("REPOSITORY Can`t add actual student exception", e)
        }
    }

    override fun addHistoryStudent(student: Student, dateOfChanging: ZonedDateTime): Int {
        LOGGER.debug("REPOSITORY add history student {}", student)
        try {
            val sql =
                "INSERT INTO student_versions (studentId, userId, surname, name, middleName, email, dateOfBirth, dateOfEntering, dateOfChanging)" +
                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            LOGGER.debug("REPOSITORY add history student params: {} sql: {}", student, sql)
            return jdbcOperations.update(
                sql,
                student.personId,
                student.userId,
                student.surname,
                student.name,
                student.middleName,
                student.email,
                TimestampzConverter.convertFrom(student.dateOfBirth),
                TimestampzConverter.convertFrom(student.dateOfEntering),
                TimestampzConverter.convertFrom(dateOfChanging)
            )
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t add history student {}", student)
            throw RepositoryException("REPOSITORY Can`t add history student exception", e)
        }
    }

    override fun updateActualStudent(student: Student): Int {
        LOGGER.debug("REPOSITORY update actual student {}", student)
        try {
            val sql = "UPDATE student " +
                    "SET surname=?, name=?, middleName=?, email=?, dateOfBirth=?, dateOfEntering=? " +
                    "WHERE studentId=?"
            LOGGER.debug("REPOSITORY update actual student params: {} sql: {}", student, sql)
            return jdbcOperations.update(
                sql,
                student.surname,
                student.name,
                student.middleName,
                student.email,
                TimestampzConverter.convertFrom(student.dateOfBirth),
                TimestampzConverter.convertFrom(student.dateOfEntering),
                student.personId
            )
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t update actual student {}", student)
            throw RepositoryException("REPOSITORY Can`t update actual student exception", e)
        }
    }

    override fun updateHistoryStudent(student: Student, dateOfChanging: ZonedDateTime): Int {
        LOGGER.debug("REPOSITORY update history student {}", student)
        try {
            val sql = "UPDATE student_versions " +
                    "SET surname=?, name=?, middleName=?, email=?, dateOfBirth=?, dateOfEntering=?, dateOfChanging=? " +
                    "WHERE studentId=?"
            LOGGER.debug("REPOSITORY update history student params: {} sql: {}", student, sql)
            return jdbcOperations.update(
                sql,
                student.surname,
                student.name,
                student.middleName,
                student.email,
                TimestampzConverter.convertFrom(student.dateOfBirth),
                TimestampzConverter.convertFrom(student.dateOfEntering),
                TimestampzConverter.convertFrom(dateOfChanging),
                student.personId
            )
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t update history student {}", student)
            throw RepositoryException("REPOSITORY Can`t update history student exception", e)
        }
    }

    override fun getActualStudent(id: UUID): Student? {
        LOGGER.debug("REPOSITORY get actual student by id {}", id)
        try {
            val sql = "SELECT * FROM student WHERE studentId = ?"
            LOGGER.debug("REPOSITORY get actual student params: {} sql: {}", id, sql)
            val student: Student? =
                jdbcOperations.queryForObject(sql, rowMapper, id)
            return student
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t get actual student by id {}", id)
            throw RepositoryException("REPOSITORY can`t get actual student by id exception", e)
        }
    }

    override fun getWithParams(filter: StudentFilter): List<Student> {
        LOGGER.debug("REPOSITORY get students by filter {}", filter)
        try {
            val sql = SqlQueryBuilder()
                .select("DISTINCT student.*")
                .from("student")
                .apply {
                    filter.groups?.takeIf { it.isNotEmpty() }?.let { groups ->
                        join("INNER", "student_group", "student.studentId = student_group.studentId", "1")
                        `in`("student_group.groupId", groups.map { it.toString() })
                    }
                    filter.rooms?.takeIf { it.isNotEmpty() }?.let { rooms ->
                        join("INNER", "room_users", "student.userId = room_users.userId", "1")
                        `in`("room_users.roomId", rooms.map { it.toString() })
                    }
                    filter.games?.takeIf { it.isNotEmpty() }?.let { games ->
                        join("INNER", "game_participants", "student.studentId = game_participants.studentId", "1")
                        `in`("game_participants.gameId", games.map { it.toString() })
                    }

                    filter.studentId?.let { where("student.studentId", it.toString()) }
                    filter.id?.let { where("student.userId", it.toString()) }
                    filter.surname?.let { where("student.surname", it) }
                    filter.name?.let { where("student.name", it) }
                    filter.middleName?.let { where("student.middleName", it) }
                    filter.email?.let { where("student.email", it) }

                    filter.dateOfBirthL?.let { start ->
                        filter.dateOfBirthR?.let { end ->
                            between(
                                "student.dateOfBirth",
                                Timestamp.from(start.toInstant()).toString(),
                                Timestamp.from(end.toInstant()).toString()
                            )
                        } ?: where("student.dateOfBirth", Timestamp.from(start.toInstant()).toString(), operand = ">=")
                    }
                    filter.dateOfBirthR?.let { end ->
                        if (filter.dateOfBirthL == null) {
                            where("student.dateOfBirth", Timestamp.from(end.toInstant()).toString(), operand = "<=")
                        }
                    }

                    filter.dateOfEnteringL?.let { start ->
                        filter.dateOfEnteringR?.let { end ->
                            between(
                                "student.dateOfEntering",
                                Timestamp.from(start.toInstant()).toString(),
                                Timestamp.from(end.toInstant()).toString()
                            )
                        } ?: where("student.dateOfEntering", Timestamp.from(start.toInstant()).toString(), operand = ">=")
                    }
                    filter.dateOfEnteringR?.let { end ->
                        if (filter.dateOfEnteringL == null) {
                            where("student.dateOfEntering", Timestamp.from(end.toInstant()).toString(), operand = "<=")
                        }
                    }
                }
                .orderBy("student.surname")
                .build()
            LOGGER.debug("REPOSITORY get students by filter: {} sql: {}", filter, sql)
            return jdbcOperations.query(sql, rowMapper)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY can`t get students by filter: {}", filter)
            throw RepositoryException("REPOSITORY can`t get students by filter exception", e)
        }
    }

    override fun deleteActualStudent(id: UUID): Int {
        LOGGER.debug("REPOSITORY delete actual student by id {}", id)
        try {
            val sql = "DELETE FROM student WHERE studentId = ?"
            LOGGER.debug("REPOSITORY delete actual student params: {} sql: {}", id, sql)
            return jdbcOperations.update(sql, id)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t delete actual student by id {}", id)
            throw RepositoryException("REPOSITORY can`t delete actual student by id exception", e)
        }
    }

    override fun deleteHistoryStudent(id: UUID, dateOfChanging: ZonedDateTime): Int {
        LOGGER.debug("REPOSITORY delete history student params: {}, {}", id, dateOfChanging)
        try {
            val sql = "DELETE FROM student_versions WHERE studentId = ? AND dateOfChanging = ?"
            LOGGER.debug("REPOSITORY delete history student params: {}, {} sql: {}", id, dateOfChanging, sql)
            return jdbcOperations.update(sql, id, TimestampzConverter.convertFrom(dateOfChanging))
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t delete history student by id {}", id)
            throw RepositoryException("REPOSITORY can`t get history student by id exception", e)
        }
    }

    override fun getHistoryStudent(id: UUID, dateOfChanging: ZonedDateTime): Student? {
        LOGGER.debug("REPOSITORY get history student by id {}", id)
        try {

            // где хранить дату изменения?

            val sql = "SELECT * FROM student_versions WHERE studentId = ? AND dateOfChanging = ?"
            LOGGER.debug("REPOSITORY get history student params: {} sql: {}", id, sql)
            val student: Student? =
                jdbcOperations.queryForObject(sql, rowMapper, id, TimestampzConverter.convertFrom(dateOfChanging))
            return student
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t get history student by id {}", id)
            throw RepositoryException("REPOSITORY can`t get history student by id exception", e)
        }
    }

    override fun addStudentGroup(
        studentGroupId: UUID,
        studentId: UUID,
        groupId: UUID,
        startDate: ZonedDateTime,
        endDate: ZonedDateTime?,
        actual: Boolean
    ): Int {
        LOGGER.debug(
            "REPOSITORY add student to group params: {}, {}, {}, {}, {}",
            studentId, groupId, startDate, endDate, actual
        )
        try {
            val sql =
                "INSERT INTO student_group (studentGroupId, studentId, groupId, startDate, endDate, actual) VALUES(?, ?, ?, ?, ?, ?)"
            LOGGER.debug(
                "REPOSITORY add student to group params: {}, {}, {}, {}, {}, {} sql: {}",
                studentGroupId, studentId, groupId, startDate, endDate, actual, sql
            )
            return jdbcOperations.update(
                sql,
                studentGroupId,
                studentId,
                groupId,
                TimestampzConverter.convertFrom(startDate),
                TimestampzConverter.convertFrom(endDate),
                actual
            )
        } catch (e: Exception) {
            LOGGER.error(
                "REPOSITORY add student to group params: {}, {}, {}, {}, {}, {}",
                studentGroupId, studentId, groupId, startDate, endDate, actual
            )
            throw RepositoryException("REPOSITORY can`t add student to group exception", e)
        }
    }

    override fun updateStudentGroup(
        studentGroupId: UUID,
        studentId: UUID,
        groupId: UUID,
        startDate: ZonedDateTime,
        endDate: ZonedDateTime?,
        actual: Boolean
    ): Int {
        LOGGER.debug(
            "REPOSITORY update student group params: {}, {}, {}, {}, {}, {}",
            studentGroupId, studentId, groupId, startDate, endDate, actual
        )
        try {
            val sql =
                "UPDATE student_group SET groupId = ?, startDate = ?, endDate = ?, actual = ? WHERE studentGroupId = ?"
            LOGGER.debug(
                "REPOSITORY update student to group params: {}, {}, {}, {}, {}, {} sql: {}",
                studentGroupId, studentId, groupId, startDate, endDate, actual, sql
            )
            return jdbcOperations.update(
                sql,
                groupId,
                TimestampzConverter.convertFrom(startDate),
                if (endDate != null) TimestampzConverter.convertFrom(endDate) else null,
                actual,
                studentGroupId
            )
        } catch (e: Exception) {
            LOGGER.error(
                "REPOSITORY update student to group params: {}, {}, {}, {}, {}, {}",
                studentGroupId, studentId, groupId, startDate, endDate, actual
            )
            throw RepositoryException("REPOSITORY can`t update student to group exception", e)
        }
    }

    //TODO(finish)
    override fun getStudentGroup(studentGroupId: UUID): StudentGroupInfo? {
        LOGGER.debug("REPOSITORY get student group info {}", studentGroupId)
        try {
            val sql = "SELECT * FROM student_group WHERE studentGroupId = ?"
            LOGGER.debug("REPOSITORY get student group info {} sql: {}", studentGroupId, sql)
            val studentGroupInfo: StudentGroupInfo? =
                jdbcOperations.queryForObject(
                    sql,
                    { resultSet: ResultSet, i: Int ->
                        StudentGroupInfo(
                            UUID.fromString(resultSet.getString("studentGroupId")),
                            UUID.fromString(resultSet.getString("studentId")),
                            UUID.fromString(resultSet.getString("groupId")),
                            "groupName",
                            TimestampzConverter.convert(resultSet.getTimestamp("startDate")) ?: ZonedDateTime.now(),
                            TimestampzConverter.convert(resultSet.getTimestamp("endDate")),
                            resultSet.getBoolean("actual"),
                            null
                        )
                    },
                    studentGroupId
                )
            return studentGroupInfo
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t get student group info {}", studentGroupId)
            throw RepositoryException("REPOSITORY can`t get student group info exception", e)
        }
    }

    override fun deleteStudentGroup(studentGroupId: UUID): Int {
        LOGGER.debug("REPOSITORY delete student group info {}", studentGroupId)
        try {
            val sql = "DELETE FROM student_group WHERE studentGroupId = ?"
            LOGGER.debug("REPOSITORY delete student group info {} sql: {}", studentGroupId, sql)
            return jdbcOperations.update(sql, studentGroupId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t delete student group info {}", studentGroupId)
            throw RepositoryException("REPOSITORY can`t delete student group info exception", e)
        }
    }

    override fun addHistoryStudentGroup(historyId: UUID, studentGroupId: UUID, dateOfChanging: ZonedDateTime): Int {
        LOGGER.debug(
            "REPOSITORY add history student to group params: {}, {}, {}",
            historyId, studentGroupId, dateOfChanging
        )
        try {
            val sql =
                "INSERT INTO history_student_group (historyId, studentGroupId, dateOfChanging) VALUES(?, ?, ?)"
            LOGGER.debug(
                "REPOSITORY add history student to group params: {}, {}, {} sql: {}",
                historyId, studentGroupId, dateOfChanging, sql
            )
            return jdbcOperations.update(
                sql,
                historyId, studentGroupId, TimestampzConverter.convertFrom(dateOfChanging),
            )
        } catch (e: Exception) {
            LOGGER.error(
                "REPOSITORY add history student to group params: {}, {}, {}",
                historyId, studentGroupId, dateOfChanging,
            )
            throw RepositoryException("REPOSITORY can`t add history student to group exception", e)
        }
    }

    override fun updateHistoryStudentGroup(historyId: UUID, studentGroupId: UUID, dateOfChanging: ZonedDateTime): Int {
        LOGGER.debug(
            "REPOSITORY update history student to group params: {}, {}, {}",
            historyId, studentGroupId, dateOfChanging
        )
        try {
            val sql =
                "UPDATE history_student_group SET studentGroupId = ?, dateOfChanging = ? WHERE historyId = ?"
            LOGGER.debug(
                "REPOSITORY update history student to group params: {}, {}, {} sql: {}",
                historyId, studentGroupId, dateOfChanging, sql
            )
            return jdbcOperations.update(
                sql,
                studentGroupId, TimestampzConverter.convertFrom(dateOfChanging), historyId
            )
        } catch (e: Exception) {
            LOGGER.error(
                "REPOSITORY update history student to group params: {}, {}, {}",
                historyId, studentGroupId, dateOfChanging
            )
            throw RepositoryException("REPOSITORY can`t update history student to group exception", e)
        }
    }

    override fun getHistoryStudentGroup(historyId: UUID): StudentGroupInfo? {
        LOGGER.debug("REPOSITORY get history student group info {}", historyId)
        try {
            val sql =
                "SELECT * FROM student_group " +
                        "JOIN history_student_group ON student_group.studentGroupId = history_student_group.studentGroupId " +
                        "WHERE history_student_group.historyId = ?"
            LOGGER.debug("REPOSITORY get history student group info {} sql: {}", historyId, sql)
            val studentGroupInfo: StudentGroupInfo? =
                jdbcOperations.queryForObject(
                    sql,
                    { resultSet: ResultSet, i: Int ->
                        StudentGroupInfo(
                            UUID.fromString(resultSet.getString("studentGroupId")),
                            UUID.fromString(resultSet.getString("studentId")),
                            UUID.fromString(resultSet.getString("groupId")),
                            getGroupName(resultSet.getString("groupId"))!!,
                            TimestampzConverter.convert(resultSet.getTimestamp("startDate")) ?: ZonedDateTime.now(),
                            TimestampzConverter.convert(resultSet.getTimestamp("endDate")),
                            resultSet.getBoolean("actual"),
                            TimestampzConverter.convert(resultSet.getTimestamp("dateOfChanging"))
                        )
                    },
                    historyId
                )
            return studentGroupInfo
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t get history student group info {}", historyId)
            throw RepositoryException("REPOSITORY can`t get history student group info exception", e)
        }
    }

    override fun deleteHistoryStudentGroup(historyId: UUID): Int {
        LOGGER.debug("REPOSITORY delete history student group info {}", historyId)
        try {
            val sql = "DELETE FROM history_student_group WHERE historyId = ?"
            LOGGER.debug("REPOSITORY delete history student group info {} sql: {}", historyId, sql)
            return jdbcOperations.update(sql, historyId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t delete history student group info {}", historyId)
            throw RepositoryException("REPOSITORY can`t delete history student group info exception", e)
        }
    }

    private fun getFacultyName(facultyId: String): String? {
        val sql = "SELECT name FROM faculty WHERE facultyId = ?"
        return jdbcOperations.queryForObject(sql, { resultSet: ResultSet, i: Int ->
            (resultSet.getString("name"))
        }, UUID.fromString(facultyId))
    }

    private fun getGroupName(groupId: String): String? {
        val sql = "SELECT name FROM \"group\" WHERE groupId = ?"
        return jdbcOperations.queryForObject(sql, { resultSet: ResultSet, i: Int ->
            (resultSet.getString("name"))
        }, UUID.fromString(groupId))
    }

}