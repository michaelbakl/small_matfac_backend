package app.core.converter.game

import app.core.converter.IConverter
import ru.baklykov.app.core.converter.datetime.ZonedDateConverter
import app.core.model.game.DifficultyLevel
import app.core.model.game.GameConfig
import app.core.model.game.GameType
import app.web.model.request.game.CreateGameRequest
import java.time.ZonedDateTime
import java.util.*

object GameConfigConverter : IConverter<GameConfig, CreateGameRequest> {
    override fun convert(obj: CreateGameRequest): GameConfig {
        return GameConfig(
            // categories = convertUUIDsToList(obj.categories),
            questionCount = obj.questionCount,
            name = obj.name,
            duration = obj.duration,
            gameType = obj.gameType?.let { GameTypeConverter.convert(it) } ?: GameType.SINGLE,
            difficulty = obj.difficulty?.let { DifficultyConverter.convert(it) } ?: DifficultyLevel.MEDIUM,
            allowSkips = obj.allowSkips,
            enableHints = obj.enableHints,
            questions = convertUUIDsToList(obj.questions),
            startDate = ZonedDateConverter.convert(obj.startDate) ?: ZonedDateTime.now(),
            finishDate = obj.finishDate?.let { ZonedDateConverter.convert(obj.finishDate) } ?: ZonedDateTime.now()
                .plusHours(1)
        )
    }

    private fun convertUUIDsToList(list: List<String>?): List<UUID> {
        val response = mutableListOf<UUID>()
        list?.let { it.map { item -> response.add(UUID.fromString(item)) } }
        return response
    }

}