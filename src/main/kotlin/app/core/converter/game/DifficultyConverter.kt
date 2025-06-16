package app.core.converter.game

import app.core.converter.IConverter
import app.core.model.game.DifficultyLevel

object DifficultyConverter: IConverter<DifficultyLevel, String> {
    override fun convert(obj: String): DifficultyLevel {
        return when (obj.lowercase()) {
            "easy" -> DifficultyLevel.EASY
            "medium" -> DifficultyLevel.MEDIUM
            "hard" -> DifficultyLevel.HARD
            else -> DifficultyLevel.MEDIUM
        }
    }
}
