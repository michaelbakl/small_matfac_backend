package app.core.handler.registration

import app.core.model.person.RegistrationRequest

interface RoleRegistrationHandler {
    /**
     * @param role
     * @return true if supports, false otherwise
     */
    fun supports(role: String): Boolean

    /**
     * @param request for user account registration
     */
    fun register(request: RegistrationRequest)
}
