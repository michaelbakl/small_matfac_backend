package app.core.repository.group

import app.web.model.response.group.GetGroupInfoResponse
import ru.baklykov.app.core.model.GroupInfo
import java.sql.Timestamp
import java.util.*

interface IGroupRepository {

    fun addGroup(groupId: UUID, name: String, dateOfCreating: Timestamp, classNum: Int): Int

    fun getGroupById(groupId: UUID): GroupInfo?

    fun getGroupsWithParams(
        groupId: UUID?,
        name: String?,
        firstDate: Timestamp?,
        secondDate: Timestamp?,
        classNum: Int?
    ): List<GetGroupInfoResponse>

    fun updateGroup(groupId: UUID, name: String, dateOfCreating: Timestamp, classNum: Int): Int

    fun deleteGroup(groupId: UUID): Int

}
