package ru.baklykov.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class GameApp

fun main(args: Array<String>) {
    runApplication<GameApp>(*args)
}
