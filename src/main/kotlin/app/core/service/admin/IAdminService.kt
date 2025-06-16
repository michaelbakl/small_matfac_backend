package app.core.service.admin

import java.util.*

interface IAdminService {

    /**
     * @param requestId - uuid of the request
     * @param action - action
     * @param reviewerId - uuid of the user who reviews request
     */
    fun reviewRequest(requestId: UUID, action: String, reviewerId: UUID)

}
