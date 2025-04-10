package app.core.repository.group

import app.core.exception.RepositoryException
import app.core.util.SqlQueryBuilder
import app.web.model.response.group.GetGroupInfoResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.RowMapper
import ru.baklykov.app.core.model.GroupInfo
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class PostgresGroupRepository(private val jdbcOperations: JdbcOperations) : IGroupRepository {

    private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)

    override fun addGroup(
        groupId: UUID,
        name: String,
        dateOfCreating: Timestamp,
        classNum: Int
    ): Int {
        try {
            LOGGER.error(
                "REPOSITORY add group by params {}, {}, {}, {}",
                groupId,
                name,
                dateOfCreating,
                classNum
            )
            val sql =
                "INSERT INTO \"group\" (groupId, name, dateOfCreating, classNum) VALUES(?, ?, ?, ?)"
            return jdbcOperations.update(sql, groupId, name, dateOfCreating, classNum)
        } catch (e: Exception) {
            LOGGER.error(
                "REPOSITORY add group by params {}, {}, {}, {} error",
                groupId,
                name,
                dateOfCreating,
                classNum,
                e
            )
            throw RepositoryException("REPOSITORY add group exception", e)
        }
    }

    override fun getGroupById(groupId: UUID): GroupInfo? {
        try {
            LOGGER.error("REPOSITORY get group by id {}", groupId)
            val sql = "SELECT * FROM \"group\" WHERE groupId=?"
            return jdbcOperations.queryForObject(sql, { resultSet: ResultSet, i: Int ->
                GroupInfo(
                    UUID.fromString(resultSet.getString("groupId")),
                    resultSet.getString("name"),
                    ZonedDateTime.ofInstant(resultSet.getTimestamp("dateOfCreating").toInstant(), ZoneId.systemDefault()),
                    UUID.fromString(resultSet.getString("facultyId")),
                    // TODO: assistants
                    listOf(),
                    resultSet.getInt("classNum")
                )
            }, groupId)
        } catch (e: Exception) {
            LOGGER.error(
                "REPOSITORY get group by id {} error",
                groupId,
                e
            )
            throw RepositoryException("REPOSITORY get group by id exception", e)
        }
    }

    override fun getGroupsWithParams(
        groupId: UUID?,
        name: String?,
        firstDate: Timestamp?,
        secondDate: Timestamp?,
        classNum: Int?
    ): List<GetGroupInfoResponse> {
        try {
            val sql = SqlQueryBuilder()
                .select("*")
                .from("\"group\"")
                .where("groupId", groupId?.toString())
                .where("name", name)
            sql.between(
                "dateOfCreating",
                firstDate?.toString(),
                secondDate?.toString()
            )
            val rowMapper: RowMapper<GetGroupInfoResponse> = RowMapper<GetGroupInfoResponse> { resultSet: ResultSet, rowIndex: Int ->
                GetGroupInfoResponse(
                    UUID.fromString(resultSet.getString("groupId")),
                    resultSet.getString("name"),
                    ZonedDateTime.ofInstant(resultSet.getTimestamp("dateOfCreating").toInstant(), ZoneId.systemDefault()),
                    resultSet.getInt("classNum")
                )
            }
            val finalSql = sql.build()
            LOGGER.debug(
                "REPOSITORY get groups with params {}, {}, {}, {}, {} sql: {}",
                groupId,
                name,
                firstDate,
                secondDate,
                classNum,
                finalSql
            )
            return jdbcOperations.query(finalSql, rowMapper)
        } catch (e: Exception) {
            LOGGER.error(
                "REPOSITORY get groups with params {}, {}, {}, {}, {} error",
                groupId,
                name,
                firstDate,
                secondDate,
                classNum,
                e
            )
            throw RepositoryException("REPOSITORY get groups with params exception", e)
        }
    }

    override fun updateGroup(
        groupId: UUID,
        name: String,
        dateOfCreating: Timestamp,
        classNum: Int
    ): Int {
        try {
            LOGGER.error(
                "REPOSITORY update group by params {}, {}, {}, {}",
                groupId,
                name,
                dateOfCreating,
                classNum
            )
            val sql = "UPDATE \"group\" SET name=?, dateOfCreating=?, semesterNum=? WHERE groupId=?"
            return jdbcOperations.update(sql, name, dateOfCreating, classNum, groupId)
        } catch (e: Exception) {
            LOGGER.error(
                "REPOSITORY update group by params {}, {}, {}, {} error",
                groupId,
                name,
                dateOfCreating,
                classNum,
                e
            )
            throw RepositoryException("REPOSITORY update group exception", e)
        }
    }

    override fun deleteGroup(groupId: UUID): Int {
        try {
            LOGGER.error("REPOSITORY delete group by params {}", groupId)
            val sql = "DELETE FROM \"group\" WHERE groupId=?"
            return jdbcOperations.update(sql, groupId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY delete group by params {} error", groupId, e)
            throw RepositoryException("REPOSITORY delete group exception", e)
        }
    }

    private fun getFacultyNameById(facultyId: UUID): String? {
        return jdbcOperations.queryForObject(
            "SELECT name FROM faculty WHERE facultyId=?",
            { resultSet: ResultSet, i: Int ->
                resultSet.getString("name")
            }, facultyId
        )
    }
}