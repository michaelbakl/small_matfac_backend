package app.core.service.group

import app.core.filter.GroupFilter
import app.web.model.response.group.GetGroupInfoResponse
import app.web.model.response.group.GetGroupsInfoResponse
import app.core.model.GroupInfo
import java.util.*

interface IGroupService {

    fun addGroup(groupInfo: GroupInfo): GetGroupInfoResponse

    fun updateGroup(groupInfo: GroupInfo): GetGroupInfoResponse

    fun getGroupById(groupId: UUID): GetGroupInfoResponse

    fun getGroupsWithParams(filter: GroupFilter): GetGroupsInfoResponse

    fun removeGroup(groupId: UUID): Int
}