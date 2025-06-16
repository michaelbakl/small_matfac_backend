package app.core.converter.game

import app.core.converter.IConverter
import app.core.model.game.GameType

object GameTypeConverter : IConverter<GameType, String> {
    override fun convert(obj: String): GameType {
        return when (obj.lowercase()) {
            "single" -> GameType.SINGLE
            "duel" -> GameType.DUEL
            "team" -> GameType.TEAM
            else -> GameType.SINGLE
        }
    }
}