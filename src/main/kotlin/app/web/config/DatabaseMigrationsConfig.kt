package app.web.config

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.FlywayException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource


@Configuration
open class DatabaseMigrationsConfig(private val dataSource: DataSource) {
    /**
     * flyway creator
     * @return Flyway
     */
    @Bean
    open fun creativesManagementFlyway(): Flyway {
        val flyway: Flyway = Flyway
            .configure()
            .dataSource(dataSource)
            .table("flyway_schema")
            .baselineOnMigrate(true)
            .locations("db/migrations")
            .load()
        try {
            flyway.migrate()
        } catch (e: FlywayException) {
            flyway.repair()
            flyway.migrate()
        }
        return flyway
    }
}
