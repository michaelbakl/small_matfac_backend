package ru.baklykov.app.core.converter.room

import app.core.converter.ITripleConverter
import app.core.exception.ConverterException
import app.core.model.Room
import app.web.model.request.room.UpdateRoomRequest
import app.web.model.response.room.GetRoomInfoResponse
import java.util.*

object UpdateRoomConverter : ITripleConverter<Room, UpdateRoomRequest, GetRoomInfoResponse> {
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
            obj.games ?: listOf(),
            dateOfCreating = obj.dateOfCreating.toString()
                ?: throw ConverterException("Cant convert Room to response. dateOfCreating is null")
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