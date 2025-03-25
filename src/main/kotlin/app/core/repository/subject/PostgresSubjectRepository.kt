package app.core.repository.subject

import app.core.exception.RepositoryException
import app.core.filter.SubjectFilter
import app.core.util.SqlQueryBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.RowMapper
import ru.baklykov.app.core.model.Subject
import java.sql.ResultSet
import java.util.*
import java.util.regex.Pattern


open class PostgresSubjectRepository(private val jdbcOperations: JdbcOperations) : ISubjectRepository {

    private val LOGGER: Logger = LoggerFactory.getLogger(PostgresSubjectRepository::class.java)

    override fun addSubject(subject: Subject): Int {
        try {
            val sql = "INSERT INTO subject (subjectId, name) VALUES (?, ?)"
            LOGGER.debug("REPOSITORY add subject params: {}, sql: {}", subject, sql)
            return jdbcOperations.update(sql, subject.id, subject.name)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t add subject {}", subject)
            throw RepositoryException("REPOSITORY Can`t add subject exception", e)
        }
    }

    override fun updateSubject(subject: Subject): Int {
        try {
            val sql = "UPDATE subject SET name = ? WHERE subjectId = ?"
            LOGGER.debug("REPOSITORY update subject params: {}, sql: {}", subject, sql)
            return jdbcOperations.update(sql, subject.name, subject.id)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t update subject {}", subject)
            throw RepositoryException("REPOSITORY Can`t update subject exception", e)
        }
    }

    override fun delete(id: UUID): Int {
        try {
            val sql = "DELETE FROM subject WHERE subjectId = ?"
            LOGGER.debug("REPOSITORY delete subject by id params: {} sql: {}", id, sql)
            return jdbcOperations.update(sql, id)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t delete subject by id params: {}", id)
            throw RepositoryException("REPOSITORY Can`t delete subject by id exception", e)
        }
    }

    override fun getById(id: UUID): Subject? {
        try {
            val sql =
                "SELECT * FROM subject JOIN semester_subject ON subject.subjectId=semester_subject.subjectId WHERE subject.subjectId = ?"
            LOGGER.debug("REPOSITORY get subject by id params: {} sql: {}", id, sql)
            val subject: Subject? = jdbcOperations.queryForObject(
                sql,
                { resultSet: ResultSet, i: Int ->
                    Subject(
                        UUID.fromString(resultSet.getString("subjectId")),
                        resultSet.getString("name")
                    )
                },
                id
            )
            return subject
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t get subject by id params: {}", id)
            throw RepositoryException("REPOSITORY Can`t get subject by id exception", e)
        }
    }

    override fun getByFilter(filter: SubjectFilter): List<Subject> {
        LOGGER.debug("REPOSITORY get subjects by filter {}", filter)
        try {
            val sql = SqlQueryBuilder()
                .select("*")
                .from("subject")
                .where("subject.subjectId", filter.id)
                .where("subject.name", filter.name)
                .build()

            val rowMapper: RowMapper<Subject> = RowMapper<Subject> { resultSet: ResultSet, rowIndex: Int ->
                Subject(
                    UUID.fromString(resultSet.getString("subjectId")),
                    resultSet.getString("name")
                )
            }
            LOGGER.debug("REPOSITORY get subject by filter: {} sql: {}", filter, sql)
            return jdbcOperations.query(sql, rowMapper)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY can`t get subjects by filter: {}", filter)
            throw RepositoryException("REPOSITORY can`t get subjects by filter exception", e)
        }
    }

    override fun getAll(): List<Subject> {
        LOGGER.debug("REPOSITORY get all subjects")
        try {
            val sql: String =
                "SELECT * FROM subject JOIN semester_subject ON subject.subjectId=semester_subject.subjectId";
            LOGGER.debug("REPOSITORY get all subject sql: {}", sql)
            val rowMapper: RowMapper<Subject> = RowMapper<Subject> { resultSet: ResultSet, rowIndex: Int ->
                Subject(
                    UUID.fromString(resultSet.getString("subjectId")),
                    resultSet.getString("name")
                )
            }
            return jdbcOperations.query(sql, rowMapper)
        } catch (e: Exception) {
            throw RepositoryException("REPOSITORY can`t get all subjects", e)
        }
    }

    private fun isUUID(testUUID: String?): Boolean {
        return if (testUUID == null) {
            false
        } else Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")
            .matcher(testUUID).find()
    }

}