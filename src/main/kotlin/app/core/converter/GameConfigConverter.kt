package ru.baklykov.app.core.converter

import app.core.converter.IConverter
import ru.baklykov.app.core.model.game.DifficultyLevel
import ru.baklykov.app.core.model.game.GameConfig
import ru.baklykov.app.core.model.game.GameType
import ru.baklykov.app.web.model.request.game.CreateGameRequest
import java.time.ZonedDateTime
import java.util.*

object GameConfigConverter: IConverter<GameConfig, CreateGameRequest> {
    override fun convert(obj: CreateGameRequest): GameConfig {
        return GameConfig(
            convertUUIDsToList(obj.categories),
            obj.questionCount,
            obj.duration,
            obj.gameType?.let { GameTypeConverter.convert(it) } ?: GameType.SINGLE,
            obj.difficulty?.let { DifficultyConverter.convert(it) } ?: DifficultyLevel.MEDIUM,
            obj.allowSkips,
            obj.enableHints,
            convertUUIDsToList(obj.questions),
            ZonedDateConverter.convert(obj.startDate) ?: ZonedDateTime.now()
        )
    }

    private fun convertUUIDsToList(list: List<String>?): List<UUID> {
        val response = mutableListOf<UUID>()
        list?.let { it.map { item -> response.add(UUID.fromString(item)) } }
        return response
    }

}