package ru.baklykov.app.core.service.room

import app.core.exception.NotFoundException
import app.core.exception.RepositoryException
import app.core.exception.ServiceException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import ru.baklykov.app.core.converter.room.RoomConverter
import ru.baklykov.app.core.filter.RoomFilter
import ru.baklykov.app.core.model.Room
import ru.baklykov.app.core.repository.game.IGameRepository
import ru.baklykov.app.core.repository.room.IRoomRepository
import ru.baklykov.app.web.model.response.room.GetRoomInfoResponse
import java.util.*

open class RoomService(private val roomRepository: IRoomRepository, private val gameRepository: IGameRepository): IRoomService {

    private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)

    override fun addRoom(room: Room): GetRoomInfoResponse {
        LOGGER.debug("SERVICE add room {}", room)
        try {
            roomRepository.addRoom(room)
            return RoomConverter.convertToResponse(roomRepository.getRoomById(room.roomId))
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE error adding room {}", room)
            throw ServiceException("SERVICE add room exception", e)
        }
    }

    @Transactional
    override fun updateRoom(room: Room): GetRoomInfoResponse {
        LOGGER.debug("SERVICE update room {}", room)
        try {
            val oldRoomInfo = roomRepository.getRoomById(room.roomId)
            if (oldRoomInfo.games?.equals(room.games) != true) {
                room.games?. let { it.map { game -> {
                    if (!oldRoomInfo.games?.contains(game)!!) {
                        gameRepository.addGame(game)
                    } else {
                        gameRepository.updateGame(game)
                    }
                } } }
            }
            return RoomConverter.convertToResponse(roomRepository.getRoomById(room.roomId))
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE error updating room {}", room)
            throw ServiceException("SERVICE update room exception", e)
        }
    }

    override fun getRoomById(roomId: String): GetRoomInfoResponse {
        LOGGER.debug("SERVICE get room by id {}", roomId)
        try {
            val room = roomRepository.getRoomById(UUID.fromString(roomId)) ?: throw NotFoundException("Room not found")
            return RoomConverter.convertToResponse(room)
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE error getting room by id {}", roomId)
            throw ServiceException("SERVICE get room by id exception", e)
        }
    }

    override fun getRoomsWithParams(filter: RoomFilter): List<GetRoomInfoResponse> {
        TODO("Not yet implemented")
    }

    override fun removeRoom(roomId: String): Int {
        try {
            LOGGER.debug("SERVICE remove room by id {}", roomId)
            return roomRepository.deleteRoomById(UUID.fromString(roomId))
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error removing room by id {}", roomId, e)
            throw ServiceException("SERVICE remove room by id exception", e)
        }
    }

    override fun openRoom(roomId: String): GetRoomInfoResponse {
        try {
            LOGGER.debug("SERVICE open room {}", roomId)
            roomRepository.updateRoomAvailability(UUID.fromString(roomId), false)
            return RoomConverter.convertToResponse(roomRepository.getRoomById(UUID.fromString(roomId)))
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error removing room by id {}", roomId, e)
            throw ServiceException("SERVICE remove room by id exception", e)
        }
    }

    override fun closeRoom(roomId: String): GetRoomInfoResponse {
        try {
            LOGGER.debug("SERVICE close room {}", roomId)
            roomRepository.updateRoomAvailability(UUID.fromString(roomId), true)
            return RoomConverter.convertToResponse(roomRepository.getRoomById(UUID.fromString(roomId)))
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error closing room {}", roomId, e)
            throw ServiceException("SERVICE close room exception", e)
        }
    }

    override fun addPlayersToRoom(roomId: String, players: List<String>): GetRoomInfoResponse {
        try {
            LOGGER.debug("SERVICE add players to room {}, {}", roomId, players)
            val room = getRoomById(roomId)
            if(room.isClosed) {
                throw ServiceException("SERVICE add players to room exception (room is closed)")
            }
            players.let {
                it.map {
                    item -> roomRepository.addParticipantToRoom(UUID.fromString(roomId), UUID.fromString(item))
                }
            }
            return RoomConverter.convertToResponse(roomRepository.getRoomById(UUID.fromString(roomId)))
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error adding players to room {}, {}", roomId,players, e)
            throw ServiceException("SERVICE add players to room exception", e)
        }
    }

    override fun removePlayersFromRoom(roomId: String, players: List<String>): GetRoomInfoResponse {
        try {
            LOGGER.debug("SERVICE remove players from room {}, {}", roomId, players)
            val room = getRoomById(roomId)
            if(room.isClosed) {
                throw ServiceException("SERVICE remove players to room exception (room is closed)")
            }
            players.let {
                it.map {
                    item -> roomRepository.removeParticipantFromRoom(UUID.fromString(roomId), UUID.fromString(item))
                }
            }
            return RoomConverter.convertToResponse(roomRepository.getRoomById(UUID.fromString(roomId)))
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error removing players from room {}, {}", roomId,players, e)
            throw ServiceException("SERVICE remove players from room exception", e)
        }
    }

    override fun updatePlayersInRoom(roomId: String, players: List<String>): GetRoomInfoResponse {
        try {
            LOGGER.debug("SERVICE update players in room {}, {}", roomId, players)
            val room = getRoomById(roomId)
            if(room.isClosed) {
                throw ServiceException("SERVICE update players in room exception (room is closed)")
            }

            val currentPlayers = room.students.toSet()
            val playerIds = players.map { UUID.fromString(it) }
            val playersToRemove = currentPlayers - playerIds.toSet()

            playersToRemove.forEach { playerId ->
                roomRepository.removeParticipantFromRoom(room.roomId, playerId)
            }

            players.let {
                it.map {
                        item -> {
                            val userId = UUID.fromString(item)
                            if (!currentPlayers.contains(userId)) {
                               roomRepository.addParticipantToRoom(room.roomId, userId)
                            }
                        }
                }
            }


            return RoomConverter.convertToResponse(roomRepository.getRoomById(UUID.fromString(roomId)))
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error updating players in room {}, {}", roomId,players, e)
            throw ServiceException("SERVICE update players in room exception", e)
        }
    }

    override fun startGame(roomId: String, gameId: String): GetRoomInfoResponse {
        try {
            LOGGER.debug("SERVICE start game by config in room {}, {}", roomId, gameId)
            val room = getRoomById(roomId)
            if(room.isClosed) {
                throw ServiceException("SERVICE can`t start game exception (room is closed)")
            }
            gameRepository.startGame(UUID.fromString(gameId))
            return RoomConverter.convertToResponse(roomRepository.getRoomById(UUID.fromString(roomId)))
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error removing players from room {}, {}", roomId, gameId, e)
            throw ServiceException("SERVICE remove players from room exception", e)
        }
    }

    override fun addGameToRoom(roomId: String, gameId: String): GetRoomInfoResponse {
        try {
            LOGGER.debug("SERVICE add game to room {}, {}", roomId, gameId)
            roomRepository.addGameToRoom(UUID.fromString(roomId), UUID.fromString(gameId))
            return RoomConverter.convertToResponse(roomRepository.getRoomById(UUID.fromString(roomId)))
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error adding game to room {}, {}", roomId, gameId, e)
            throw ServiceException("SERVICE add game to room exception", e)
        }
    }

    override fun removeGameFromRoom(roomId: String, gameId: String): GetRoomInfoResponse {
        try {
            LOGGER.debug("SERVICE remove game from room {}, {}", roomId, gameId)

            roomRepository.removeGameFromRoom(UUID.fromString(roomId), UUID.fromString(gameId))
            return RoomConverter.convertToResponse(roomRepository.getRoomById(UUID.fromString(roomId)))
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error removing game from room {}, {}", roomId, gameId, e)
            throw ServiceException("SERVICE remove game from room exception", e)
        }
    }

    override fun checkGameExists(roomId: String, gameId: String): Boolean {
        try {
            LOGGER.debug("SERVICE check game exists in the room {}, {}", roomId, gameId)
            val room = roomRepository.getRoomById(UUID.fromString(roomId))



            // TODO: finish method


            return true
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error checking game exists in the room {}, {}", roomId, gameId, e)
            throw ServiceException("SERVICE check game exists in the room exception", e)
        }    }
}