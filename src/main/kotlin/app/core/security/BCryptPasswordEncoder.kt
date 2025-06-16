package app.core.security

import org.mindrot.jbcrypt.BCrypt
import org.springframework.stereotype.Component

@Component
open class BCryptPasswordEncoder: PasswordEncoder {

    override fun matches(plainPassword: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
