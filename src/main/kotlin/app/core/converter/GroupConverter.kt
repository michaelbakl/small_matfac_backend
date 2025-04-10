package app.core.converter

import app.web.model.request.group.AddGroupRequest
import app.web.model.response.group.GetGroupInfoResponse
import ru.baklykov.app.core.converter.ZonedDateConverter
import ru.baklykov.app.core.model.GroupInfo
import java.time.ZonedDateTime
import java.util.*

//TODO: finish
object GroupConverter: ITripleConverter<GroupInfo, AddGroupRequest, GetGroupInfoResponse> {
    override fun convertToModel(obj: AddGroupRequest): GroupInfo {
        return GroupInfo(
            UUID.randomUUID(),
            obj.name,
            ZonedDateConverter.convert(obj.dateOfCreating)?: ZonedDateTime.now(),
            UUID.randomUUID(),
            listOf(),
            obj.classNum
        )
    }

    override fun convertToResponse(obj: GroupInfo): GetGroupInfoResponse {
        return GetGroupInfoResponse(
            obj.groupId,
            obj.name,
            obj.dateOfCreating,
            obj.classNum
        )
    }

    override fun convertToResponseList(list: List<GroupInfo>?): List<GetGroupInfoResponse> {
        val result: MutableList<GetGroupInfoResponse> = ArrayList()
        list?. let { it.map { item -> result.add(convertToResponse(item)) } }
        return result
    }
}