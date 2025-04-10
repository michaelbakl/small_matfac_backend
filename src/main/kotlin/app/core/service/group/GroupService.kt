package app.core.service.group

import app.core.converter.GroupConverter
import app.core.exception.NotFoundException
import app.core.exception.RepositoryException
import app.core.exception.ServiceException
import app.core.filter.GroupFilter
import app.core.repository.group.IGroupRepository
import app.web.model.response.group.GetGroupInfoResponse
import app.web.model.response.group.GetGroupsInfoResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.baklykov.app.core.model.GroupInfo
import java.sql.Timestamp
import java.util.*

@Service
class GroupService(private val repository: IGroupRepository) : IGroupService {

    private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)

    override fun addGroup(groupInfo: GroupInfo): GetGroupInfoResponse {
        try {
            LOGGER.debug("SERVICE add group {}", groupInfo)
            repository.addGroup(
                groupInfo.groupId,
                groupInfo.name,
                Timestamp.valueOf(groupInfo.dateOfCreating.toLocalDateTime()),
                groupInfo.classNum
                )
            return getGroupById(groupInfo.groupId)
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error adding group {}", groupInfo, e)
            throw ServiceException("SERVICE add group exception", e)
        }
    }

    override fun updateGroup(groupInfo: GroupInfo): GetGroupInfoResponse {
        try {
            LOGGER.debug("SERVICE update group {}", groupInfo)
            repository.updateGroup(
                groupInfo.groupId,
                groupInfo.name,
                Timestamp.valueOf(groupInfo.dateOfCreating.toLocalDateTime()),
                groupInfo.classNum
            )
            return getGroupById(groupInfo.groupId)
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error updating group {}", groupInfo, e)
            throw ServiceException("SERVICE update group exception", e)
        }
    }

    override fun getGroupById(groupId: UUID): GetGroupInfoResponse {
        try {
            LOGGER.debug("SERVICE get group by id {}", groupId)
            val groupInfo = repository.getGroupById(groupId) ?: throw NotFoundException("Group not found")
            return GroupConverter.convertToResponse(groupInfo)
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error getting group by id {}", groupId, e)
            throw ServiceException("SERVICE getting group by id exception", e)
        }
    }

    override fun getGroupsWithParams(filter: GroupFilter): GetGroupsInfoResponse {
        try {
            LOGGER.debug("SERVICE get group by filter {}", filter)
            val groupInfo = repository.getGroupsWithParams(
                filter.groupId,
                filter.name,
                filter.firstDate,
                filter.secondDate,
                filter.classNum
            )
            return GetGroupsInfoResponse(groupInfo)
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error getting group by filter {}", filter, e)
            throw ServiceException("SERVICE getting group by filter exception", e)
        }
    }

    override fun removeGroup(groupId: UUID): Int {
        try {
            LOGGER.debug("SERVICE remove group by id {}", groupId)
            return repository.deleteGroup(groupId)
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error removing group by id {}", groupId, e)
            throw ServiceException("SERVICE remove group by id exception", e)
        }
    }
}
