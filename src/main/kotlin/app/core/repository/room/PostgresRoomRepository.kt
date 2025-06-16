package ru.baklykov.app.core.repository.room

import app.core.exception.RepositoryException
import app.core.util.SqlQueryBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.stereotype.Repository
import ru.baklykov.app.core.converter.datetime.TimestampzConverter
import ru.baklykov.app.core.converter.datetime.ZonedDateConverter
import ru.baklykov.app.core.converter.util.IdConverter
import app.core.model.Room
import java.sql.ResultSet
import java.time.ZonedDateTime
import java.util.*

@Repository
open class PostgresRoomRepository(val jdbcOperations: JdbcOperations) : IRoomRepository {

    private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)

    private val rowRoomMapper = { resultSet: ResultSet, i: Int ->
        Room(
            UUID.fromString(resultSet.getString("roomId")),
            resultSet.getString("name"),
            UUID.fromString(resultSet.getString("teacherId")),
            getStudentsInRoom(UUID.fromString(resultSet.getString("roomId"))),
            resultSet.getBoolean("isClosed"),
            getGamesInTheRoom(UUID.fromString(resultSet.getString("roomId"))),
            ZonedDateConverter.convert(resultSet.getString("dateOfCreating"))
        )
    }

    override fun addRoom(room: Room): Int {
        try {
            LOGGER.debug("REPOSITORY create room {}", room)
            val sql =
                "INSERT INTO room (roomId, name, teacherId, isClosed, dateOfCreating) VALUES(?, ?, ?, ?, ?)"
            return jdbcOperations.update(
                sql,
                room.roomId,
                room.name,
                room.teacherId,
                room.isClosed,
                TimestampzConverter.convertFrom(room.dateOfCreating)
            )
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY create room {} error", room, e)
            throw RepositoryException("REPOSITORY add room exception", e)
        }
    }

    override fun updateRoom(room: Room): Int {
        try {
            LOGGER.debug("REPOSITORY update room {}", room)
            val sql =
                "UPDATE room SET name=?, teacherId=?, isClosed=? WHERE roomId=?"
            return jdbcOperations.update(sql, room.name, room.teacherId, room.isClosed, room.roomId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY update room {} error", room, e)
            throw RepositoryException("REPOSITORY update room exception", e)
        }
    }

    override fun getRoomById(id: UUID): Room {
        try {
            LOGGER.debug("REPOSITORY get room by id {}", id)
            val sql = "SELECT * FROM room WHERE roomId=?"
            return jdbcOperations.queryForObject(sql, rowRoomMapper, id)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY get room by id {} error", id, e)
            throw RepositoryException("REPOSITORY get room by id exception", e)
        }
    }

    override fun getRoomsWithParams(
        id: UUID?,
        name: String?,
        teacherId: UUID?,
        students: List<UUID>?,
        isClosed: Boolean?,
        startDate: ZonedDateTime?,
        finishDate: ZonedDateTime?
    ): List<Room> {
        try {
            val sql = SqlQueryBuilder()
                .select("*")
                .from("room")
                .join(joinType = "LEFT", "room_users", "room.roomId=room_users.roomId", IdConverter.convertFrom(id))
                .where("room.roomId", IdConverter.convertFrom(id))
                .where("name", name)
                .where("teacherId", IdConverter.convertFrom(teacherId))
                .where("isClosed", isClosed?.toString())
            sql.between(
                "dateOfCreating",
                TimestampzConverter.convertFrom(startDate)?.toString(),
                TimestampzConverter.convertFrom(finishDate)?.toString()
            )
            students.let { it?.map { item -> sql.where("userId", item.toString()) } }
            val finalSql = sql.build()

            LOGGER.debug(
                "REPOSITORY get rooms with params {}, {}, {}, {}, {}, {}, {} sql: {}",
                id,
                name,
                teacherId,
                students,
                isClosed,
                startDate,
                finishDate,
                finalSql
            )
            return jdbcOperations.query(finalSql, rowRoomMapper)
        } catch (e: Exception) {
            LOGGER.error(
                "REPOSITORY get rooms with params {}, {}, {}, {}, {}, {}, {} error",
                id,
                name,
                teacherId,
                students,
                isClosed,
                startDate,
                finishDate,
                e
            )
            throw RepositoryException("REPOSITORY get rooms with params exception", e)
        }
    }

    override fun deleteRoomById(id: UUID): Int {
        try {
            LOGGER.debug("REPOSITORY remove room by id {}", id)
            val sql = "DELETE FROM room WHERE roomId=?"
            return jdbcOperations.update(sql, id)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY remove room by id {} error", id, e)
            throw RepositoryException("REPOSITORY remove room by id exception", e)
        }
    }

    override fun updateRoomAvailability(roomId: UUID, isClosed: Boolean): Int {
        try {
            LOGGER.debug(
                "REPOSITORY update room availability by params {}, {}",
                roomId,
                isClosed
            )
            val sql = "UPDATE room SET isClosed=? WHERE roomId=?"
            return jdbcOperations.update(sql, roomId, isClosed)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY update room availability by params {}, {} error", roomId, isClosed, e)
            throw RepositoryException("REPOSITORY room availability exception", e)
        }
    }

    override fun addParticipantToRoom(roomId: UUID, studentId: UUID): Int {
        try {
            LOGGER.debug("REPOSITORY add participants to room {}, {}", roomId, studentId)
            val sql =
                "INSERT INTO room_users (roomId, studentId) VALUES(?, ?)"
            return jdbcOperations.update(sql, roomId, studentId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY add participants to room {}, {} error", roomId, studentId, e)
            throw RepositoryException("REPOSITORY add participants to room exception", e)
        }
    }

    override fun removeParticipantFromRoom(roomId: UUID, studentId: UUID): Int {
        try {
            LOGGER.debug("REPOSITORY remove participants from room {}, {}", roomId, studentId)
            val sql = "DELETE FROM room_users WHERE roomId=? AND studentId=?"
            return jdbcOperations.update(sql, roomId, studentId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY remove participants from room {}, {} error", roomId, studentId, e)
            throw RepositoryException("REPOSITORY remove participants from room exception", e)
        }
    }

    override fun isStudentInRoom(roomId: UUID, studentId: UUID): Boolean {
        try {
            LOGGER.debug("REPOSITORY check if student is in the room {}, {}", roomId, studentId)
            val sql = """
            SELECT COUNT(*) FROM room_users 
            WHERE roomId = ? AND userId = ? AND role = 'STUDENT'
            """.trimIndent()
            val count = jdbcOperations.queryForObject(sql, Int::class.java, roomId, studentId)
            return count != null && count > 0
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY check if student is in the room exception {}, {}", roomId, studentId)
            throw RepositoryException("REPOSITORY remove participants from room exception", e)
        }
    }

    fun getStudentsInRoom(roomId: UUID): List<UUID> {
        val sql = "SELECT * FROM room_users WHERE roomId=?"
        return jdbcOperations.query(
            sql,
            { resultSet: ResultSet, i: Int ->
                UUID.fromString(resultSet.getString("studentId"))
            }, roomId
        )
    }

    fun getGamesInTheRoom(roomId: UUID): List<UUID> {
        val sql = "SELECT * FROM game WHERE roomId=?"
        return jdbcOperations.query(
            sql,
            { resultSet: ResultSet, i: Int ->
                UUID.fromString(resultSet.getString("gameId"))
            }, roomId
        )
    }
}
