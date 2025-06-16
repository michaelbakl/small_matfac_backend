package app.web.security

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*


/**
 * user credentials implementation
 */
class UserCredentialsImpl(
    @JsonProperty("userId") override val userId: String, @JsonProperty(
        "email"
    ) override val email: String, roles: Collection<String>
) :
    UserCredentials {

    @JsonProperty("roles")
    override val roles: Set<String> = Collections.unmodifiableSet(roles.toSet())

}

