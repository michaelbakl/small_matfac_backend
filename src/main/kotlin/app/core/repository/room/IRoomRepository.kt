package ru.baklykov.app.core.repository.room

import ru.baklykov.app.core.model.Question
import ru.baklykov.app.core.model.Room
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.*

interface IRoomRepository {

    /**
     * adds room to database
     * @param room - room to add
     * @return added room
     */
    fun addRoom(room: Room): Room

    /**
     * updates room information
     * @param room - room with new information
     * @return updated room with new info
     */
    fun updateRoom(room: Room): Room

    /**
     * finds room by id
     * @param id - UUID of the room
     * @return found room or null
     */
    fun getRoomById(id: UUID): Room

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
        teacherId: UUID,
        students: List<UUID>?,
        questions: List<Question>?,
        isClosed: Boolean?,
        startDate: LocalDateTime?,
        finishDate: LocalDateTime?
    )

    /**
     * deletes room by id
     * @param id - id of the room
     * @return 1 if deleted, 0 if not
     */
    fun deleteRoomById(id: UUID): Int

    /**
     * updates room dates of starting and finishing
     * @param roomId - id of the room
     * @param startDate - date of starting the room
     * @param finishDate - date when room become unavailable
     * @return updated room
     */
    fun updateRoomDates(roomId: UUID, startDate: ZonedDateTime, finishDate: ZonedDateTime): Int

    /**
     * updates room availability
     * @param roomId - id of the room
     * @param isClosed - true if closed, false if available
     * @return updated room
     */
    fun updateRoomAvailability(roomId: UUID, isClosed: Boolean): Int

    /**
     * adds questions to the room
     * @param roomId - id of the room
     * @param questionId - question id to add to the room
     * @return updated room
     */
    fun addQuestionToRoom(roomId: UUID, questionId: UUID): Int

    /**
     * adds participant to the room
     * @param roomId - id of the room
     * @param studentId - id of the student to add
     * @return updated room
     */
    fun addParticipantToRoom(roomId: UUID, studentId: UUID): Int

    /**
     * adds questions to the room
     * @param roomId - id of the room
     * @param questionId - question id to remove to the room
     * @return updated room
     */
    fun removeQuestionFromRoom(roomId: UUID, questionId: UUID): Int

    /**
     * adds participant to the room
     * @param roomId - id of the room
     * @param studentId - id of the student to remove
     * @return updated room
     */
    fun removeParticipantFromRoom(roomId: UUID, studentId: UUID): Int

    /**
     * adds existing game to room
     * @param roomId - id of the room
     * @param gameId - id of the game to be added to the room
     * @return 1 if success, 0 otherwise
     */
    fun addGameToRoom(roomId: UUID, gameId: UUID): Int

    /**
     * removes existing game to room
     * @param roomId - id of the room
     * @param gameId - id of the game to be added to the room
     * @return 1 if success, 0 otherwise
     */
    fun removeGameFromRoom(roomId: UUID, gameId: UUID): Int

}
