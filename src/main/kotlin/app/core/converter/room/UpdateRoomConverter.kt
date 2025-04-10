package ru.baklykov.app.core.converter.room

import app.core.converter.ITripleConverter
import ru.baklykov.app.core.converter.GameConverter
import ru.baklykov.app.core.model.Room
import ru.baklykov.app.web.model.request.room.UpdateRoomRequest
import ru.baklykov.app.web.model.response.room.GetRoomInfoResponse
import java.util.*

object UpdateRoomConverter:ITripleConverter<Room, UpdateRoomRequest, GetRoomInfoResponse> {
    override fun convertToModel(obj: UpdateRoomRequest): Room {
        return Room(
            UUID.randomUUID(),
            obj.name,
            UUID.fromString(obj.teacherId),
            convertListOfStringsToListOfUUID(obj.participants),
            obj.isClosed ?: false,
            listOf()
        )
    }

    override fun convertToResponse(obj: Room): GetRoomInfoResponse {
        return GetRoomInfoResponse(
            obj.roomId,
            obj.name,
            obj.teacherId,
            obj.students,
            obj.isClosed,
            obj.games?: listOf()
        )
    }

    override fun convertToResponseList(list: List<Room>?): List<GetRoomInfoResponse>? {
        TODO("Not yet implemented")
    }

    private fun convertListOfStringsToListOfUUID(list: List<String>): List<UUID> {
        val result = mutableListOf<UUID>()
        list.let { it.map { item -> result.add(UUID.fromString(item)) } }
        return result
    }
}