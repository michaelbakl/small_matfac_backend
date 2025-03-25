package app.core.security

import org.mindrot.jbcrypt.BCrypt;

class BCryptPasswordEncoder: PasswordEncoder {

    override fun matches(plainPassword: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}