package app.web.security


/**
 * Annotation for check by one of roles existence
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AuthRolesRequired(
    /**
     * Returns a role which is required to access the method
     * @return the role name
     */
    vararg val value: String
)
