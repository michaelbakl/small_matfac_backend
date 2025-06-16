package app.core.repository.room

import app.core.exception.NotFoundException
import app.core.exception.RepositoryException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.RowMapper
import app.core.model.Room
import ru.baklykov.app.core.repository.room.PostgresRoomRepository
import java.sql.ResultSet
import java.time.ZonedDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class PostgresRoomRepositoryTest {

    @Mock
    private lateinit var jdbcOperations: JdbcOperations

    @Mock
    private lateinit var resultSet: ResultSet

    @InjectMocks
    private lateinit var repository: PostgresRoomRepository

    private lateinit var testRoomId: UUID
    private lateinit var testTeacherId: UUID
    private lateinit var testStudentId: UUID
    private lateinit var testGameId: UUID
    private lateinit var testRoom: Room
    private lateinit var testDate: ZonedDateTime

    @BeforeEach
    fun setUp() {
        testRoomId = UUID.randomUUID()
        testTeacherId = UUID.randomUUID()
        testStudentId = UUID.randomUUID()
        testGameId = UUID.randomUUID()
        testDate = ZonedDateTime.now()

        testRoom = Room(
            roomId = testRoomId,
            name = "Test Room",
            teacherId = testTeacherId,
            students = listOf(testStudentId),
            isClosed = false,
            games = listOf(testGameId),
            dateOfCreating = testDate
        )
    }

    @Test
    fun `addRoom should return 1 when successful`() {
        whenever(jdbcOperations.update(any(), any(), any(), any(), any(), any())).thenReturn(1)

        val result = repository.addRoom(testRoom)

        assertEquals(1, result)
        verify(jdbcOperations).update(
            any(),
            eq(testRoom.roomId),
            eq(testRoom.name),
            eq(testRoom.teacherId),
            eq(testRoom.isClosed),
            any()
        )
    }

    @Test
    fun `addRoom should throw RepositoryException when error occurs`() {
        whenever(jdbcOperations.update(any(), any(), any(), any(), any(), any()))
            .thenThrow(RuntimeException("DB error"))

        assertThrows<RepositoryException> { repository.addRoom(testRoom) }
    }

    @Test
    fun `updateRoom should return 1 when successful`() {
        whenever(jdbcOperations.update(any(), any(), any(), any(), any())).thenReturn(1)

        val result = repository.updateRoom(testRoom)

        assertEquals(1, result)
        verify(jdbcOperations).update(
            any(),
            eq(testRoom.name),
            eq(testRoom.teacherId),
            eq(testRoom.isClosed),
            eq(testRoom.roomId)
        )
    }

    @Test
    fun `updateRoom should throw RepositoryException when error occurs`() {
        whenever(jdbcOperations.update(any(), any(), any(), any(), any()))
            .thenThrow(RuntimeException("DB error"))

        assertThrows<RepositoryException> { repository.updateRoom(testRoom) }
    }

    @Test
    fun `getRoomById should return room when found`() {
        whenever(resultSet.getString("roomId")).thenReturn(testRoomId.toString())
        whenever(resultSet.getString("name")).thenReturn(testRoom.name)
        whenever(resultSet.getString("teacherId")).thenReturn(testTeacherId.toString())
        whenever(resultSet.getBoolean("isClosed")).thenReturn(false)
        whenever(resultSet.getString("dateOfCreating")).thenReturn("2023-01-01T00:00:00Z")

        whenever(jdbcOperations.queryForObject(any<String>(), any<RowMapper<Room>>(), eq(testRoomId)))
            .thenReturn(testRoom)
        whenever(jdbcOperations.query(any<String>(), any<RowMapper<UUID>>(), eq(testRoomId)))
            .thenReturn(listOf(testStudentId))
        whenever(jdbcOperations.query(any<String>(), any<RowMapper<UUID>>(), eq(testRoomId)))
            .thenReturn(listOf(testGameId))

        val result = repository.getRoomById(testRoomId)

        assertNotNull(result)
        assertEquals(testRoomId, result.roomId)
        assertEquals(testTeacherId, result.teacherId)
    }

    @Test
    fun `getRoomById should throw NotFoundException when room not found`() {
        whenever(jdbcOperations.queryForObject(any<String>(), any<RowMapper<UUID>>(), eq(testRoomId)))
            .thenReturn(null)

        assertThrows<NotFoundException> { repository.getRoomById(testRoomId) }
    }

    @Test
    fun `getRoomById should throw RepositoryException when error occurs`() {
        whenever(jdbcOperations.query(any<String>(), any(), eq(testRoomId)))
            .thenThrow(RuntimeException("DB error"))

        assertThrows<RepositoryException> { repository.getRoomById(testRoomId) }
    }

    @Test
    fun `getRoomsWithParams should return rooms when found`() {
        val students = listOf(testStudentId)
        val startDate = testDate.minusDays(1)
        val finishDate = testDate.plusDays(1)

        whenever(jdbcOperations.query(any<String>(), any<RowMapper<Room>>())).thenReturn(listOf(testRoom))
        whenever(jdbcOperations.query(any<String>(), any<RowMapper<UUID>>(), eq(testRoomId)))
            .thenReturn(students)
        whenever(jdbcOperations.query(any<String>(), any<RowMapper<UUID>>(), eq(testRoomId)))
            .thenReturn(listOf(testGameId))

        val result = repository.getRoomsWithParams(
            id = testRoomId,
            name = "Test",
            teacherId = testTeacherId,
            students = students,
            isClosed = false,
            startDate = startDate,
            finishDate = finishDate
        )

        assertFalse(result.isEmpty())
        assertEquals(testRoomId, result[0].roomId)
    }

    @Test
    fun `getRoomsWithParams should return rooms with partial params`() {
        whenever(jdbcOperations.query(any<String>(), any<RowMapper<Room>>())).thenReturn(listOf(testRoom))
        whenever(jdbcOperations.query(any<String>(), any<RowMapper<UUID>>(), eq(testRoomId)))
            .thenReturn(emptyList())
        whenever(jdbcOperations.query(any<String>(), any<RowMapper<UUID>>(), eq(testRoomId)))
            .thenReturn(emptyList())

        val result = repository.getRoomsWithParams(
            id = null,
            name = "Test",
            teacherId = null,
            students = null,
            isClosed = null,
            startDate = null,
            finishDate = null
        )

        assertFalse(result.isEmpty())
    }

    @Test
    fun `getRoomsWithParams should throw RepositoryException when error occurs`() {
        whenever(jdbcOperations.query(any<String>(), any()))
            .thenThrow(RuntimeException("DB error"))

        assertThrows<RepositoryException> {
            repository.getRoomsWithParams(null, null, null, null, null, null, null)
        }
    }

    @Test
    fun `deleteRoomById should return 1 when successful`() {
        whenever(jdbcOperations.update(any<String>(), eq(testRoomId))).thenReturn(1)

        val result = repository.deleteRoomById(testRoomId)

        assertEquals(1, result)
        verify(jdbcOperations).update(any<String>(), eq(testRoomId))
    }

    @Test
    fun `deleteRoomById should throw RepositoryException when error occurs`() {
        whenever(jdbcOperations.update(any<String>(), eq(testRoomId)))
            .thenThrow(RuntimeException("DB error"))

        assertThrows<RepositoryException> { repository.deleteRoomById(testRoomId) }
    }

    @Test
    fun `updateRoomAvailability should return 1 when successful`() {
        whenever(jdbcOperations.update(any<String>(), anyBoolean(), eq(testRoomId))).thenReturn(1)

        val result = repository.updateRoomAvailability(testRoomId, true)

        assertEquals(1, result)
        verify(jdbcOperations).update(any<String>(), eq(true), eq(testRoomId))
    }

    @Test
    fun `updateRoomAvailability should throw RepositoryException when error occurs`() {
        whenever(jdbcOperations.update(any<String>(), anyBoolean(), eq(testRoomId)))
            .thenThrow(RuntimeException("DB error"))

        assertThrows<RepositoryException> {
            repository.updateRoomAvailability(testRoomId, true)
        }
    }

    @Test
    fun `addParticipantToRoom should return 1 when successful`() {
        whenever(jdbcOperations.update(any<String>(), eq(testRoomId), eq(testStudentId))).thenReturn(1)

        val result = repository.addParticipantToRoom(testRoomId, testStudentId)

        assertEquals(1, result)
        verify(jdbcOperations).update(any<String>(), eq(testRoomId), eq(testStudentId))
    }

    @Test
    fun `addParticipantToRoom should throw RepositoryException when error occurs`() {
        whenever(jdbcOperations.update(any<String>(), eq(testRoomId), eq(testStudentId)))
            .thenThrow(RuntimeException("DB error"))

        assertThrows<RepositoryException> {
            repository.addParticipantToRoom(testRoomId, testStudentId)
        }
    }

    @Test
    fun `removeParticipantFromRoom should return 1 when successful`() {
        whenever(jdbcOperations.update(any<String>(), eq(testRoomId), eq(testStudentId))).thenReturn(1)

        val result = repository.removeParticipantFromRoom(testRoomId, testStudentId)

        assertEquals(1, result)
        verify(jdbcOperations).update(any<String>(), eq(testRoomId), eq(testStudentId))
    }

    @Test
    fun `removeParticipantFromRoom should throw RepositoryException when error occurs`() {
        whenever(jdbcOperations.update(any<String>(), eq(testRoomId), eq(testStudentId)))
            .thenThrow(RuntimeException("DB error"))

        assertThrows<RepositoryException> {
            repository.removeParticipantFromRoom(testRoomId, testStudentId)
        }
    }

    @Test
    fun `getStudentsInRoom should return students list`() {
        whenever(jdbcOperations.query(any<String>(), any<RowMapper<UUID>>(), eq(testRoomId)))
            .thenReturn(listOf(testStudentId))

        val result = repository.getStudentsInRoom(testRoomId)

        assertFalse(result.isEmpty())
        assertEquals(testStudentId, result[0])
    }

    @Test
    fun `getGamesInTheRoom should return games list`() {
        whenever(jdbcOperations.query(any<String>(), any<RowMapper<UUID>>(), eq(testRoomId)))
            .thenReturn(listOf(testGameId))

        val result = repository.getGamesInTheRoom(testRoomId)

        assertFalse(result.isEmpty())
        assertEquals(testGameId, result[0])
    }
}