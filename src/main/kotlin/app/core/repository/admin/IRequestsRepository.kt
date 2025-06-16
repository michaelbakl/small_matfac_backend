package app.core.repository.admin

import app.core.model.person.RegistrationRequest
import java.util.*

interface IRequestsRepository {

    /**
     * Saves a new registration request.
     * @param request the registration request to save
     */
    fun save(request: RegistrationRequest): Int

    /**
     * Finds a registration request by its ID.
     * @param id the ID of the request
     * @return the registration request or null if not found
     */
    fun findById(id: UUID): RegistrationRequest?

    /**
     * Checks if a request with the given email already exists.
     * @param email the email to check
     * @return true if a request with the email exists
     */
    fun existsByEmail(email: String): Boolean

    /**
     * Updates the status and review metadata of a request.
     * @param id the ID of the request
     * @param status new status: APPROVED or REJECTED
     * @param reviewerId the ID of the reviewer
     */
    fun updateStatus(id: UUID, status: String, reviewerId: UUID)

    /**
     * Returns all registration requests with status PENDING.
     * @return list of pending registration requests
     */
    fun findAllPending(): List<RegistrationRequest>
}
