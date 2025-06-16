package app.web.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource


@Configuration
open class DatabaseConfig {
    @Value("\${spring.datasource.driver-class-name}")
    private val driverClassName: String? = null

    @Value("\${spring.datasource.url}")
    private val databaseUrl: String? = null

    @Value("\${spring.datasource.username}")
    private val username: String? = null

    @Value("\${spring.datasource.password}")
    private val password: String? = null

    /**
     * data source
     * @return data source
     */
    @Bean
    @Primary
    open fun dataSource(): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName(driverClassName ?: "org.postgresql.Driver")
        dataSource.url = databaseUrl
        dataSource.username = username
        dataSource.password = password
        return dataSource
    }

    /**
     * template
     * @param dataSource - data source
     * @return JdbcOperations
     */
    @Bean
    @Primary
    open fun template(dataSource: DataSource): JdbcOperations {
        return JdbcTemplate(dataSource)
    }
}
