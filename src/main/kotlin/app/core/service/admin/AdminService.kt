package app.core.service.admin

import app.core.exception.NotFoundException
import app.core.exception.RepositoryException
import app.core.exception.ServiceException
import app.core.handler.registration.RoleRegistrationHandler
import app.core.model.request.RequestStatus
import app.core.model.request.ReviewAction
import app.core.repository.admin.IRequestsRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.util.*

@Service
open class AdminService(
    private val registrationRequestRepo: IRequestsRepository,
    private val handlers: List<RoleRegistrationHandler>
) : IAdminService {

    @Transactional(rollbackFor = [NotFoundException::class, RepositoryException::class, ServiceException::class])
    override fun reviewRequest(requestId: UUID, action: String, reviewerId: UUID) {
        val request = registrationRequestRepo.findById(requestId)
            ?: throw NotFoundException("Registration request not found")

        val actionEnum = ReviewAction.entries.find { it.name == action.uppercase() }
            ?: throw IllegalArgumentException("Unknown action: $action")

        when (actionEnum) {
            ReviewAction.APPROVE -> {
                val handler = handlers.find { it.supports(request.requestedRole) }
                    ?: throw IllegalStateException("No handler found for role: ${request.requestedRole}")
                handler.register(request)
                request.status = RequestStatus.APPROVED.name
            }

            ReviewAction.REJECT -> {
                request.status = RequestStatus.REJECTED.name
            }
        }

        request.reviewerId = reviewerId
        request.reviewedAt = ZonedDateTime.now().toInstant()

        registrationRequestRepo.save(request)
    }

}
