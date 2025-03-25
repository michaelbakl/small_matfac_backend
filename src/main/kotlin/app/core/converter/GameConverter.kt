package ru.baklykov.app.core.converter

import app.core.converter.ITripleConverter
import ru.baklykov.app.core.model.game.DifficultyLevel
import ru.baklykov.app.core.model.game.Game
import ru.baklykov.app.core.model.game.GameType
import ru.baklykov.app.web.model.response.game.GetGameInfoResponse
import java.util.ArrayList

object GameConverter: ITripleConverter<Game, AddGameRequest, GetGameInfoResponse> {
    override fun convertToModel(obj: AddGameRequest): Game {
        TODO("Not yet implemented")
    }

    override fun convertToResponseList(list: List<Game>?): List<GetGameInfoResponse> {
        val result: MutableList<GetGameInfoResponse> = ArrayList()
        list?.let { it.map { item -> result.add(convertToResponse(item)) } }
        return result
    }

    override fun convertToResponse(obj: Game): GetGameInfoResponse {
        return GetGameInfoResponse(
            obj.gameId,
            obj.roomId,
            QuestionConverter.convertToResponseList(obj.questions),
            obj.config.categories ?: listOf(),
            obj.config.questionCount ?: 30,
            obj.config.duration ?: 30,
            obj.config.gameType ?: GameType.SINGLE,
            obj.config.difficulty ?: DifficultyLevel.EASY,
            obj.config.allowSkips,
            obj.config.enableHints,
            obj.status,
            obj.startDate,
            obj.finishDate,
            obj.studentsAnswers,
            obj.studentsResults
        )
    }
}