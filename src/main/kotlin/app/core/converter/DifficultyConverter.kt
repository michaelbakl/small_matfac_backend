package ru.baklykov.app.core.converter

import app.core.converter.IConverter
import ru.baklykov.app.core.model.game.DifficultyLevel

object DifficultyConverter: IConverter<DifficultyLevel, String> {
    override fun convert(obj: String): DifficultyLevel {
        return when (obj) {
            "easy" -> DifficultyLevel.EASY
            "medium" -> DifficultyLevel.MEDIUM
            "hard" -> DifficultyLevel.HARD
            else -> DifficultyLevel.MEDIUM
        }
    }
}
