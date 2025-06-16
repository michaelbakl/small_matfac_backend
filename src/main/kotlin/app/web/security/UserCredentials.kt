package app.web.security

/**
 * user credentials class
 */
interface UserCredentials {
    /**
     * returns id of user
     * @return userId
     */
    val userId: String

    /**
     * returns user email
     * @return email
     */
    val email: String?

    /**
     * returns roles of user
     * @return set of roles
     */
    val roles: Set<String>
}