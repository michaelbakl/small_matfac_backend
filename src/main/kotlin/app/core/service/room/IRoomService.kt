package ru.baklykov.app.core.service.room

import ru.baklykov.app.core.filter.RoomFilter
import app.core.model.Room
import app.web.model.response.room.GetRoomInfoResponse

interface IRoomService {

    fun addRoom(room: Room): GetRoomInfoResponse

    fun updateRoom(room: Room): GetRoomInfoResponse

    fun getRoomById(roomId: String): GetRoomInfoResponse

    fun getRoomsWithParams(filter: RoomFilter): List<GetRoomInfoResponse>

    fun removeRoom(roomId: String): Int

    /**
     * makes room available
     * @param roomId - id of the room
     */
    fun openRoom(roomId: String): GetRoomInfoResponse

    fun closeRoom(roomId: String): GetRoomInfoResponse

    fun addPlayersToRoom(roomId: String, players: List<String>): GetRoomInfoResponse

    fun removePlayersFromRoom(roomId: String, players: List<String>): GetRoomInfoResponse

    fun updatePlayersInRoom(roomId: String, players: List<String>): GetRoomInfoResponse

    fun startGame(roomId: String, gameId: String): GetRoomInfoResponse

    fun removeGameFromRoom(roomId: String, gameId: String): GetRoomInfoResponse

    /**
     * checks if game exists in room
     * @param roomId - uuid of the room
     * @param gameId - uuid of the game
     * @return true if game exists in room, false otherwise
     */
    fun checkGameExists(roomId: String, gameId: String): Boolean

    fun isUserInRoom(roomId: String, userId: String): Boolean

}
