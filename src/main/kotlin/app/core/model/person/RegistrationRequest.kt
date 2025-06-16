package app.core.model.person

import java.time.Instant
import java.util.*

data class RegistrationRequest(
    val id: UUID,
    val email: String,
    val passwordHash: String,
    val surname: String,
    val name: String,
    val middleName: String? = null,
    val requestedRole: String,
    var status: String,
    val submittedAt: Instant,
    var reviewedAt: Instant? = null,
    var reviewerId: UUID? = null
)
