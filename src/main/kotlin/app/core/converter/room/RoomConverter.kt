package ru.baklykov.app.core.converter.room

import app.core.converter.ITripleConverter
import app.core.exception.ConverterException
import app.core.model.Room
import app.web.model.request.room.AddRoomRequest
import app.web.model.response.room.GetRoomInfoResponse
import java.util.*

object RoomConverter : ITripleConverter<Room, AddRoomRequest, GetRoomInfoResponse> {
    override fun convertToModel(obj: AddRoomRequest): Room {
        return Room(
            UUID.randomUUID(),
            obj.name,
            UUID.fromString(obj.teacherId),
            convertListOfStringsToListOfUUID(obj.students),
            obj.isClosed ?: false,
            listOf(),
            dateOfCreating = null
        )
    }

    override fun convertToResponse(obj: Room): GetRoomInfoResponse {
        return GetRoomInfoResponse(
            obj.roomId,
            obj.name,
            obj.teacherId,
            obj.students,
            obj.isClosed,
            obj.games ?: listOf(),
            obj.dateOfCreating.toString()
                ?: throw ConverterException("Cant convert object of class Room to response. dateOfCreating is null")
        )
    }

    override fun convertToResponseList(list: List<Room>?): List<GetRoomInfoResponse> {
        val result = mutableListOf<GetRoomInfoResponse>()
        list?.let { it.map { item -> result.add(convertToResponse(item)) } }
        return result
    }

    private fun convertListOfStringsToListOfUUID(list: List<String>): List<UUID> {
        val result = mutableListOf<UUID>()
        list.let { it.map { item -> result.add(UUID.fromString(item)) } }
        return result
    }
}