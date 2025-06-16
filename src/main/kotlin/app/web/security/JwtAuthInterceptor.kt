package app.web.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.lang.NonNull
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import java.lang.reflect.Method


/**
 * Spring interceptor for JWT based authentication and authorization
 */
class JwtAuthInterceptor

/**
 * constructor
 * @param jwtService - jwt service
 */(
    private val jwtService: JwtTokenService
) : HandlerInterceptor {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    override fun preHandle(
        @NonNull request: HttpServletRequest,
        @NonNull response: HttpServletResponse,
        @NonNull handler: Any
    ): Boolean {
        if (handler is HandlerMethod) {
            val method: Method = handler.method
            return checkAuthorization(method, request, response)
        }
        return true
    }

    private fun getUserCredentials(request: HttpServletRequest): UserCredentials? {
        return try {
            val header = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return null
            val bearerLength = 7
            val token = header.substring(bearerLength)
            val credentials = jwtService.parseToken(token)
            logger.debug("Found credentials in Authorization header: {}", credentials.userId)
            request.setAttribute(USER_CREDENTIALS, credentials)
            credentials
        } catch (e: Exception) {
            null
        }
    }

    private fun checkAuthorization(
        method: Method,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Boolean {
        return try {
            val userCredentials = getUserCredentials(request)
            if (method.isAnnotationPresent(AuthRolesRequired::class.java)) {
                if (userCredentials == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                    return false
                }
                val annotation: AuthRolesRequired = method.getAnnotation(AuthRolesRequired::class.java)
                val requiredRoles = annotation.value.toSet()
                val userRoles = userCredentials.roles.toSet()

                val isAuthorized = userRoles.any { it in requiredRoles }
                if (!isAuthorized) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Not enough permissions")
                    return false
                }
            }
            true
        } catch (e: Exception) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Auth check failed")
            false
        }
    }

    companion object {
        /**
         * define of user credentials
         */
        const val USER_CREDENTIALS = "userCredentialsAttr"
    }
}

