package ru.baklykov.app.core.converter

import app.core.converter.ITripleConverter
import ru.baklykov.app.core.model.game.DifficultyLevel
import ru.baklykov.app.core.model.game.Game
import ru.baklykov.app.core.model.game.GameType
import ru.baklykov.app.web.model.request.game.CreateGameRequest
import ru.baklykov.app.web.model.response.game.GetGameInfoResponse
import java.time.ZonedDateTime
import java.util.ArrayList

object GameConverter: ITripleConverter<Game, CreateGameRequest, GetGameInfoResponse> {
    override fun convertToModel(obj: CreateGameRequest): Game {
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
            obj.config.startDate?: ZonedDateTime.now(),
            obj.config.finishDate?: ZonedDateTime.now(),
            obj.studentsAnswers,
            obj.studentsResults
        )
    }
}