package app.web.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter



/**
 * Configures [CorsFilter] for the application.
 *
 *
 * Include this configuration to you app, for example, like this:
 * <pre>`public class CorsConfig {
 * }
`</pre> *
 *
 *
 * Then you can change allowed origins, methods and headers by defining the environment variables:
 * <dl>
 * <dt>CORS_ALLOWED_ORIGINS</dt>
 * <dd>comma separated list of allowed origins,
 * "*" by default which means all origins are allowed (don't do it on production for non-public APIs)</dd>
 * <dt>CORS_ALLOWED_METHODS</dt>
 * <dd>comma separated list of allowed methods, "*" by default which means all methods are allowed</dd>
 * <dt>CORS_ALLOWED_HEADERS</dt>
 * <dd>comma separated list of allowed headers, "*" by default which means all headers are allowed</dd>
 * <dt>CORS_ALLOW_CREDENTIALS</dt>
 * <dd>value for Access-Control-Allow-Credentials response header, "true" by default</dd>
 * <dt>CORS_PATH</dt>
 * <dd>path pattern where to apply the CORS filter, "/ **" by default</dd>
</dl> *
 */
@Configuration
open class CorsConfiguration {

    @Value("\${CORS_ALLOWED_ORIGINS:*}")
    private val allowedOrigins: List<String> = listOf("*")

    @Value("\${CORS_ALLOWED_METHODS:*}")
    private val allowedMethods: List<String> = listOf("*")

    @Value("\${CORS_ALLOWED_HEADERS:*}")
    private val allowedHeaders: List<String> = listOf("*")

    @Value("\${CORS_ALLOW_CREDENTIALS:true}")
    private val allowCredentials: Boolean = true

    @Value("\${CORS_PATH:/**}")
    private val path: String = "/**"

    /**
     * cors filter
     * @return CorsFilter
     */
    // https://stackoverflow.com/questions/45677048/how-do-i-enable-cors-headers-in-the-swagger-v2-api-docs-offered-by-springfox-sw
    // https://stackoverflow.com/questions/66060750/cors-error-when-using-corsfilter-and-spring-security
    @Bean
    open fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()

        val config = org.springframework.web.cors.CorsConfiguration()
        config.allowCredentials = allowCredentials
        config.allowedOriginPatterns = allowedOrigins
        config.allowedMethods = allowedMethods
        config.allowedHeaders = allowedHeaders

        source.registerCorsConfiguration(path, config)
        return CorsFilter(source)
    }

    private fun toString(config: CorsConfiguration): String {
        return "CorsConfiguration(" +
                "allowCredentials=" + config.allowCredentials +
                ", allowedOrigins=" + config.allowedOrigins +
                ", allowedMethods=" + config.allowedMethods +
                ", allowedHeaders=" + config.allowedHeaders +
                ")"
    }
}

