package app.core.repository.answer

import app.core.exception.RepositoryException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.stereotype.Repository
import app.core.model.Answer
import ru.baklykov.app.core.model.Picture
import java.sql.ResultSet
import java.util.*

@Repository
open class PostgresAnswerRepository(private val jdbcOperations: JdbcOperations) : IAnswerRepository {

    private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)

    private val rowMapper = { resultSet: ResultSet, i: Int ->
        Answer(
            UUID.fromString(resultSet.getString("answerId")),
            resultSet.getString("description"),
            //getAnswerPictures(resultSet.getString("answerId"))
        )
    }

    override fun addAnswer(answer: Answer): Int {
        try {
            LOGGER.debug("REPOSITORY add answer {}", answer)
            val sqlAnswer = "INSERT INTO answer (answerId, description) VALUES(?, ?)"
            return jdbcOperations.update(sqlAnswer, answer.answerId, answer.description)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY add answer {} error", answer, e)
            throw RepositoryException("REPOSITORY add answer exception", e)
        }
    }

    override fun getAnswerById(answerId: UUID): Answer? {
        try {
            LOGGER.debug("REPOSITORY get answer by id {}", answerId)
            val sql = "SELECT * FROM answer WHERE answerId=?"
            return jdbcOperations.queryForObject(sql, rowMapper, answerId)
        } catch (e: Exception) {
            LOGGER.error(
                "REPOSITORY get answer by id {} error",
                answerId,
                e
            )
            throw RepositoryException("REPOSITORY get answer by id exception", e)
        }
    }

    override fun updateAnswer(answer: Answer): Int {
        try {
            LOGGER.debug("REPOSITORY update answer {}", answer)
            val sql = "UPDATE answer SET description=? WHERE answerId=?"
            return jdbcOperations.update(sql, answer.description, answer.answerId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY update answer {} error", answer, e)
            throw RepositoryException("REPOSITORY update answer exception", e)
        }
    }

    override fun removeAnswer(answerId: UUID): Int {
        try {
            LOGGER.debug("REPOSITORY remove answer by id {}", answerId)
            val sql = "DELETE FROM answer WHERE answerId=?"
            return jdbcOperations.update(sql, answerId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY remove answer by id {} error", answerId, e)
            throw RepositoryException("REPOSITORY remove answer by id exception", e)
        }
    }

    override fun addPictureToAnswer(answerId: UUID, pictureId: UUID): Int {
        try {
            LOGGER.debug("REPOSITORY add picture to answer {}, {}", answerId, pictureId)
            val sql = "INSERT INTO answerpictures (answerId, pictureId) VALUES (?, ?)"
            return jdbcOperations.update(sql, answerId, pictureId)
        } catch (e: Exception) {
            LOGGER.error(
                "REPOSITORY add picture to answer {}, {} error",
                answerId,
                pictureId,
                e
            )
            throw RepositoryException("REPOSITORY add picture to answer exception", e)
        }
    }

    // TODO: finish
    override fun updateAnswerPictures(answerId: UUID, pictures: List<UUID>): Int {
        try {
            LOGGER.debug("REPOSITORY update pictures in answer {}, {}", answerId, pictures)
            val sql = ""
            return jdbcOperations.update(sql, answerId, pictures)
        } catch (e: Exception) {
            LOGGER.error(
                "REPOSITORY update pictures in answer {}, {} error",
                answerId,
                pictures,
                e
            )
            throw RepositoryException("REPOSITORY update pictures in answer exception", e)
        }
    }

    override fun removePictureFromAnswer(answerId: UUID, pictureId: UUID): Int {
        try {
            LOGGER.debug("REPOSITORY remove picture from answer {}, {}", answerId, pictureId)
            val sql = "DELETE FROM answerpictures WHERE answerId=? AND pictureId=?"
            return jdbcOperations.update(sql, answerId, pictureId)
        } catch (e: Exception) {
            LOGGER.error(
                "REPOSITORY remove picture from answer {}, {} error",
                answerId,
                pictureId,
                e
            )
            throw RepositoryException("REPOSITORY remove picture from answer exception", e)
        }
    }

    override fun findByText(text: String): Answer? {
        LOGGER.debug("REPOSITORY find answer by text: {}", text)
        try {
            val sql = "SELECT * FROM answer WHERE description = ? LIMIT 1"
            return jdbcOperations.queryForObject(sql, rowMapper, text)
        } catch (e: EmptyResultDataAccessException) {
            return null
        }
        catch (e: DataAccessException) {
            LOGGER.debug("REPOSITORY Error finding answer by text: {}", text)
            throw RepositoryException("REPOSITORY cant fund answer by text", e)
        }
    }


    private fun getAnswerPictures(answerId: String): List<Picture> {
        val listRes = mutableListOf<Picture>()
        jdbcOperations.query(
            "SELECT * FROM answerpictures" +
                    " JOIN picture ON answerpictures.pictureId=picture.pictureId WHERE answerId = ?",
            { rs ->
                listRes.add(
                    Picture(
                        UUID.fromString(rs.getString("pictureId")),
                        rs.getString("picture"),
                        rs.getTimestamp("dateOfSaving").toLocalDateTime()
                    )
                )
            }, UUID.fromString(answerId)
        )
        return listRes
    }
}