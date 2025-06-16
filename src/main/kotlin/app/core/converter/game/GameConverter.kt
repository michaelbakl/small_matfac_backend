package app.core.converter.game

import app.core.converter.ITripleConverter
import app.core.converter.question.QuestionConverter
import app.core.model.game.DifficultyLevel
import app.core.model.game.Game
import app.core.model.game.GameType
import app.web.model.request.game.CreateGameRequest
import app.web.model.response.game.GetGameInfoResponse
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
            obj.config.name?: "Game",
            QuestionConverter.convertToResponseList(obj.questions),
            //obj.config.categories ?: listOf(),
            obj.config.questionCount ?: 30,
            obj.config.duration ?: 30,
            obj.config.gameType ?: GameType.SINGLE,
            obj.config.difficulty ?: DifficultyLevel.EASY,
            obj.config.allowSkips,
            obj.config.enableHints,
            obj.status,
            obj.config.startDate?: ZonedDateTime.now(),
            obj.config.finishDate?: ZonedDateTime.now()
        )
    }
}