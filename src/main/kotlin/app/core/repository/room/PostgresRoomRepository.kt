package ru.baklykov.app.core.repository.room

import app.core.exception.RepositoryException
import app.core.util.SqlQueryBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcOperations
import ru.baklykov.app.core.model.question.Question
import ru.baklykov.app.core.model.Room
import java.sql.ResultSet
import java.time.LocalDateTime
import java.util.*

class PostgresRoomRepository(val jdbcOperations: JdbcOperations): IRoomRepository {

    private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)

    private val rowRoomMapper = { resultSet: ResultSet, i: Int ->
        Room(
            UUID.fromString(resultSet.getString("roomId")),
            resultSet.getString("name"),
            UUID.fromString(resultSet.getString("teacherId")),
            getStudentsInRoom(UUID.fromString(resultSet.getString("roomId"))),
            resultSet.getBoolean("isClosed"),
            getGamesInTheRoom(UUID.fromString(resultSet.getString("roomId"))),
            resultSet.getString("dateOfCreating")
        )
    }

    override fun addRoom(room: Room): Int {
        try {
            LOGGER.error("REPOSITORY create room {}", room)
            val sql =
                "INSERT INTO room (roomId, name, teacherId, isClosed) VALUES(?, ?, ?, ?)"
            return jdbcOperations.update(sql, room.roomId, room.name, room.teacherId, room.isClosed)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY create room {} error", room, e)
            throw RepositoryException("REPOSITORY add room exception", e)
        }
    }

    override fun updateRoom(room: Room): Int {
        try {
            LOGGER.error("REPOSITORY update room {}", room)
            val sql =
                "UPDATE room SET name=?, teacherId=?, isClosed=? WHERE roomId=?"
            return jdbcOperations.update(sql, room.name, room.teacherId, room.isClosed, room.roomId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY update room {} error", room, e)
            throw RepositoryException("REPOSITORY update room exception", e)
        }
    }

    override fun getRoomById(id: UUID): Room? {
        try {
            LOGGER.error("REPOSITORY get room by id {}", id)
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
        questions: List<Question>?,
        isClosed: Boolean?,
        startDate: LocalDateTime?,
        finishDate: LocalDateTime?
    ): List<Room> {
        try {
            val sql = SqlQueryBuilder()
                .select("*")
                .from("room")
                .where("roomId", id?.toString())
                .where("name", name)
                .where("teacherId", teacherId?.toString())
            sql.between(
                "dateOfCreating",
                startDate?.toString(),
                finishDate?.toString()
            )
            val finalSql = sql.build()
            LOGGER.debug(
                "REPOSITORY get rooms with params {}, {}, {}, {}, {}, {}, {}, {} sql: {}",
                id,
                name,
                teacherId,
                students,
                questions,
                isClosed,
                startDate,
                finishDate,
                finalSql
            )
            return jdbcOperations.query(finalSql, rowRoomMapper)
        } catch (e: Exception) {
            LOGGER.error(
                "REPOSITORY get rooms with params {}, {}, {}, {}, {}, {}, {}, {} error",
                id,
                name,
                teacherId,
                students,
                questions,
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
            LOGGER.error("REPOSITORY remove room by id {}", id)
            val sql = "DELETE FROM room WHERE roomId=?"
            return jdbcOperations.update(sql)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY remove room by id {} error", id, e)
            throw RepositoryException("REPOSITORY remove room by id exception", e)
        }
    }

    override fun updateRoomAvailability(roomId: UUID, isClosed: Boolean): Int {
        try {
            LOGGER.error(
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
            LOGGER.error("REPOSITORY add participants to room {}, {}", roomId, studentId)
            val sql =
                "INSERT INTO room_students (roomId, studentId) VALUES(?, ?)"
            return jdbcOperations.update(sql, roomId, studentId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY add participants to room {}, {} error", roomId, studentId, e)
            throw RepositoryException("REPOSITORY add participants to room exception", e)
        }
    }

    override fun removeParticipantFromRoom(roomId: UUID, studentId: UUID): Int {
        try {
            LOGGER.error("REPOSITORY remove participants from room {}, {}", roomId, studentId)
            val sql = "DELETE FROM room_students WHERE roomId=? AND studentId=?"
            return jdbcOperations.update(sql, roomId, studentId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY remove participants from room {}, {} error", roomId, studentId, e)
            throw RepositoryException("REPOSITORY remove participants from room exception", e)
        }
    }

    private fun getStudentsInRoom(roomId: UUID): List<UUID> {
        val sql = "SELECT * FROM room_students WHERE roomId=?"
        return jdbcOperations.query(sql,
            { resultSet: ResultSet, i: Int ->
            UUID.fromString(resultSet.getString("studentId"))
        }, roomId
        )
    }

    private fun getGamesInTheRoom(roomId: UUID): List<UUID> {
        val sql = "SELECT * FROM game WHERE roomId=?"
        return jdbcOperations.query(sql,
            { resultSet: ResultSet, i: Int ->
                UUID.fromString(resultSet.getString("gameId"))
            }, roomId
        )
    }
}
