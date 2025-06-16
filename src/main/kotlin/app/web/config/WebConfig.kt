package app.web.config

import app.web.security.JwtAuthInterceptor
import app.web.security.JwtTokenService
import app.web.security.UserCredentialsResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


/**
 * configurer class
 */
@Configuration
open class WebConfig(
    private val jwtTokenService: JwtTokenService
) : WebMvcConfigurer {
    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        argumentResolvers.add(UserCredentialsResolver())
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(
            JwtAuthInterceptor(jwtTokenService)
        )
    }
}

