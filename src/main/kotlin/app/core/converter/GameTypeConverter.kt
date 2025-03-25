package ru.baklykov.app.core.converter

import app.core.converter.IConverter
import ru.baklykov.app.core.model.game.DifficultyLevel
import ru.baklykov.app.core.model.game.GameType

object GameTypeConverter : IConverter<GameType, String> {
    override fun convert(obj: String): GameType {
        return when (obj) {
            "single" -> GameType.SINGLE
            "duel" -> GameType.DUEL
            "team" -> GameType.TEAM
            else -> GameType.SINGLE
        }
    }
}