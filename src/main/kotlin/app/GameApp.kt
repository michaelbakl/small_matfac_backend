package app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication(scanBasePackages = ["app", "ru.baklykov"])
@EnableTransactionManagement
open class GameApp

fun main(args: Array<String>) {
    runApplication<GameApp>(*args)
}
