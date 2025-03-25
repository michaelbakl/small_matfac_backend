package app.core.security

interface PasswordEncoder {

    /**
     * Checks the entered password matches with the hashed password
     * @param plainPassword the entered plain text password
     * @param hashedPassword the stored hashed password
     * @return true if the password matches with the hash
     */
    fun matches(plainPassword: String, hashedPassword: String): Boolean

}