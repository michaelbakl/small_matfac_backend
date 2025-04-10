package ru.baklykov.app.core.repository.room

import ru.baklykov.app.core.model.question.Question
import ru.baklykov.app.core.model.Room
import java.time.LocalDateTime
import java.util.*

interface IRoomRepository {

    /**
     * adds room to database
     * @param room - room to add
     * @return added room
     */
    fun addRoom(room: Room): Int

    /**
     * updates room information
     * @param room - room with new information
     * @return updated room with new info
     */
    fun updateRoom(room: Room): Int

    /**
     * finds room by id
     * @param id - UUID of the room
     * @return found room or null
     */
    fun getRoomById(id: UUID): Room?

    /**
     * @param id - room id
     * @param name - room name
     * @param teacherId - id of creator
     * @param students - participants in the room
     * @param questions - questions in the room
     * @param isClosed - true if closed, false if available
     * @param startDate - date of starting the room (room is available when start)
     * @param finishDate - date of closing the room (becomes unavailable)
     */
    fun getRoomsWithParams(
        id: UUID?,
        name: String?,
        teacherId: UUID?,
        students: List<UUID>?,
        questions: List<Question>?,
        isClosed: Boolean?,
        startDate: LocalDateTime?,
        finishDate: LocalDateTime?
    ): List<Room>

    /**
     * deletes room by id
     * @param id - id of the room
     * @return 1 if deleted, 0 if not
     */
    fun deleteRoomById(id: UUID): Int

    /**
     * updates room availability
     * @param roomId - id of the room
     * @param isClosed - true if closed, false if available
     * @return updated room
     */
    fun updateRoomAvailability(roomId: UUID, isClosed: Boolean): Int

    /**
     * adds participant to the room
     * @param roomId - id of the room
     * @param studentId - id of the student to add
     * @return updated room
     */
    fun addParticipantToRoom(roomId: UUID, studentId: UUID): Int

    /**
     * adds participant to the room
     * @param roomId - id of the room
     * @param studentId - id of the student to remove
     * @return updated room
     */
    fun removeParticipantFromRoom(roomId: UUID, studentId: UUID): Int

}
