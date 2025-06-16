package app.core.repository.teacher

import app.core.exception.RepositoryException
import app.core.filter.TeacherFilter
import app.core.util.SqlQueryBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.baklykov.app.core.converter.datetime.TimestampzConverter
import app.core.model.person.Teacher
import java.sql.ResultSet
import java.time.ZonedDateTime
import java.util.*

@Repository
open class PostgresTeacherRepository(private val jdbcOperations: JdbcOperations) : ITeacherRepository {
    private val LOGGER: Logger = LoggerFactory.getLogger(PostgresTeacherRepository::class.java)

    val rowMapper: RowMapper<Teacher> = RowMapper<Teacher> { resultSet: ResultSet, i: Int ->
        Teacher(
            UUID.fromString(resultSet.getString("teacherId")),
            UUID.fromString(resultSet.getString("userId")),
            resultSet.getString("surname"),
            resultSet.getString("name"),
            resultSet.getString("middleName"),
            resultSet.getString("email"),
            TimestampzConverter.convert(resultSet.getTimestamp("dateOfBirth")),
            TimestampzConverter.convert(resultSet.getTimestamp("dateOfEntering"))
        )
    }

    override fun getTeacherId(userId: UUID): UUID? {
        LOGGER.debug("REPOSITORY get teacher id by user id {}", userId)
        try {
            val sql = "SELECT teacherId FROM teacher WHERE userId = ?"
            LOGGER.debug("REPOSITORY get teacher id by user id: {} sql: {}", userId, sql)
            return jdbcOperations.queryForObject(sql, { resultSet: ResultSet, i: Int ->
                UUID.fromString(resultSet.getString("teacherId"))
            }, userId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t get teacher id by user id {}", userId)
            throw RepositoryException("REPOSITORY Can`t get teacher id exception", e)
        }
    }

    @Transactional(rollbackFor = [DataAccessException::class, RepositoryException::class])
    override fun addActualTeacher(teacher: Teacher): Int {
        LOGGER.debug("REPOSITORY add actual teacher {}", teacher)
        try {
            val sql =
                "INSERT INTO teacher (teacherId, userId, surname, name, middleName, email, dateOfBirth)" +
                        " VALUES (?, ?, ?, ?, ?, ?, ?)"
            LOGGER.debug("REPOSITORY add teacher params: {} sql: {}", teacher, sql)
            return jdbcOperations.update(
                sql,
                teacher.personId,
                teacher.userId,
                teacher.surname,
                teacher.name,
                teacher.middleName,
                teacher.email,
                TimestampzConverter.convertFrom(teacher.dateOfBirth)
            )
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t add actual teacher {}", teacher)
            throw RepositoryException("REPOSITORY Can`t add actual teacher exception", e)
        }
    }

    @Transactional(rollbackFor = [DataAccessException::class, RepositoryException::class])
    override fun addHistoryTeacher(teacher: Teacher, dateOfChanging: ZonedDateTime): Int {
        LOGGER.debug("REPOSITORY add history teacher {}", teacher)
        try {
            val sql =
                "INSERT INTO teacher_versions (teacherId, userId, surname, name, middleName, email, dateOfBirth, dateOfChanging)" +
                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            LOGGER.debug("REPOSITORY add history teacher params: {} sql: {}", teacher, sql)
            return jdbcOperations.update(
                sql,
                teacher.personId,
                teacher.userId,
                teacher.surname,
                teacher.name,
                teacher.middleName,
                teacher.email,
                TimestampzConverter.convertFrom(teacher.dateOfBirth),
                TimestampzConverter.convertFrom(dateOfChanging)
            )
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t add history teacher {}", teacher)
            throw RepositoryException("REPOSITORY Can`t add history teacher exception", e)
        }
    }

    @Transactional(rollbackFor = [DataAccessException::class, RepositoryException::class])
    override fun updateActualTeacher(teacher: Teacher): Int {
        LOGGER.debug("REPOSITORY update actual teacher {}", teacher)
        try {
            val sql = "UPDATE teacher " +
                    "SET surname=?, name=?, middleName=?, email=?, dateOfBirth=? " +
                    "WHERE teacherId=?"
            LOGGER.debug("REPOSITORY update actual teacher params: {} sql: {}", teacher, sql)
            return jdbcOperations.update(
                sql,
                teacher.surname,
                teacher.name,
                teacher.middleName,
                teacher.email,
                TimestampzConverter.convertFrom(teacher.dateOfBirth),
                teacher.personId
            )
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t update actual teacher {}", teacher)
            throw RepositoryException("REPOSITORY Can`t update actual teacher exception", e)
        }
    }

    @Transactional(rollbackFor = [DataAccessException::class, RepositoryException::class])
    override fun updateHistoryTeacher(teacher: Teacher, dateOfChanging: ZonedDateTime): Int {
        LOGGER.debug("REPOSITORY update history teacher {}", teacher)
        try {
            val sql = "UPDATE teacher_versions " +
                    "SET surname=?, name=?, middleName=?, email=?, dateOfBirth=?, dateOfChanging=? " +
                    "WHERE teacherId=?"
            LOGGER.debug("REPOSITORY update history teacher params: {} sql: {}", teacher, sql)
            return jdbcOperations.update(
                sql,
                teacher.surname,
                teacher.name,
                teacher.middleName,
                teacher.email,
                TimestampzConverter.convertFrom(teacher.dateOfBirth),
                TimestampzConverter.convertFrom(dateOfChanging),
                teacher.personId
            )
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t update history teacher {}", teacher)
            throw RepositoryException("REPOSITORY Can`t update history teacher exception", e)
        }
    }

    override fun getHistoryTeacher(id: UUID, dateOfChanging: ZonedDateTime): Teacher? {
        LOGGER.debug("REPOSITORY get history teacher by id {}", id)
        try {

            // Где хранить дату изменения?
            // Исправить. Будет выдавать несколько записей

            val sql = "SELECT * FROM teacher_versions WHERE teacherId = ? AND dateOfChanging = ?"
            LOGGER.debug("REPOSITORY get history teacher params: {} sql: {}", id, sql)
            val teacher: Teacher? =
                jdbcOperations.queryForObject(
                    sql,
                    rowMapper,
                    id, TimestampzConverter.convertFrom(dateOfChanging)
                )
            return teacher
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t get history teacher by id {}", id)
            throw RepositoryException("REPOSITORY can`t get history teacher by id exception", e)
        }
    }

    override fun getActualTeacher(id: UUID): Teacher? {
        LOGGER.debug("REPOSITORY get actual teacher by id {}", id)
        try {
            val sql = "SELECT * FROM teacher WHERE teacherId = ?"
            LOGGER.debug("REPOSITORY get actual teacher params: {} sql: {}", id, sql)
            val teacher: Teacher? =
                jdbcOperations.queryForObject(
                    sql,
                    rowMapper,
                    id
                )
            return teacher
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t get actual teacher by id {}", id)
            throw RepositoryException("REPOSITORY can`t get actual teacher by id exception", e)
        }
    }

    override fun getWithParams(filter: TeacherFilter): List<Teacher> {
        LOGGER.debug("REPOSITORY get teachers by filter {}", filter)
        try {
            val sql = SqlQueryBuilder()
                .select("*")
                .from("teacher")
                .where("teacherId", filter.teacherId?.toString())
                .where("userId", filter.id?.toString())
                .where("surname", filter.surname)
                .where("name", filter.name)
                .where("middleName", filter.middleName)
                .where("email", filter.email)
                .between("dateOfBirth", filter.dateOfBirthL.toString(), filter.dateOfBirthR.toString())
                .build()
            LOGGER.debug("REPOSITORY get teachers by filter: {} sql: {}", filter, sql)
            return jdbcOperations.query(sql, rowMapper)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY can`t get teachers by filter: {}", filter)
            throw RepositoryException("REPOSITORY can`t get teachers by filter exception", e)
        }
    }

    @Transactional(rollbackFor = [DataAccessException::class, RepositoryException::class])
    override fun deleteActualTeacher(id: UUID): Int {
        LOGGER.debug("REPOSITORY delete actual teacher by id {}", id)
        try {
            val sql = "DELETE FROM teacher WHERE teacherId = ?"
            LOGGER.debug("REPOSITORY delete actual teacher params: {} sql: {}", id, sql)
            return jdbcOperations.update(sql, id)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t delete actual teacher by id {}", id)
            throw RepositoryException("REPOSITORY can`t get actual teacher by id exception", e)
        }
    }

    @Transactional(rollbackFor = [DataAccessException::class, RepositoryException::class])
    override fun deleteHistoryTeacher(id: UUID, dateOfChanging: ZonedDateTime): Int {
        LOGGER.debug("REPOSITORY delete history teacher params: {}, {}", id, dateOfChanging)
        try {
            val sql = "DELETE FROM teacher_versions WHERE teacherId = ? AND dateOfChanging = ?"
            LOGGER.debug("REPOSITORY delete history teacher params: {}, {} sql: {}", id, dateOfChanging, sql)
            return jdbcOperations.update(sql, id, TimestampzConverter.convertFrom(dateOfChanging))
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t delete history teacher by id {}", id)
            throw RepositoryException("REPOSITORY can`t get history teacher by id exception", e)
        }
    }

    override fun getAllActualTeachers(): List<Teacher> {
        LOGGER.debug("REPOSITORY get all actual teachers")
        try {
            val sql =
                "SELECT * FROM teacher"
            LOGGER.debug("REPOSITORY get all actual teachers sql: {}", sql)
            return jdbcOperations.query(sql, rowMapper)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t get all actual teachers")
            throw RepositoryException("REPOSITORY Can`t get all actual teachers exception", e)
        }
    }

    private fun getPositionNameByPositionId(positionId: UUID): String? {
        return jdbcOperations.queryForObject(
            "SELECT name FROM position WHERE positionId = ?",
            { resultSet: ResultSet, i: Int ->
                resultSet.getString("name")
            },
            positionId
        )
    }


}