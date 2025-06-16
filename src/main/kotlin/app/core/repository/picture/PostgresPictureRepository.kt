package ru.baklykov.app.core.repository.picture

import app.core.exception.RepositoryException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.stereotype.Repository
import ru.baklykov.app.core.model.Picture
import java.sql.ResultSet
import java.util.*

@Repository
open class PostgresPictureRepository(private val jdbcOperations: JdbcOperations): IPictureRepository {
    private val LOGGER: Logger = LoggerFactory.getLogger(PostgresPictureRepository::class.java)

    override fun addPicture(picture: Picture): Int {
        LOGGER.debug("REPOSITORY add picture to database {}", picture)
        try {
            val sql = "INSERT INTO picture (pictureId, picture) VALUES (?, ?)"
            LOGGER.debug("REPOSITORY add picture: {} sql: {}", picture, sql)
            return jdbcOperations.update(
                sql,
                picture.pictureId,
                picture.picture
            )
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t add picture {}", picture)
            throw RepositoryException("REPOSITORY Can`t add picture exception", e)
        }
    }

    override fun updatePicture(picture: Picture): Int {
        LOGGER.debug("REPOSITORY update picture {}", picture)
        try {
            val sql = "UPDATE picture " +
                    "SET picture=?" +
                    "WHERE pictureId=?"
            LOGGER.debug("REPOSITORY update picture params: {} sql: {}", picture, sql)
            return jdbcOperations.update(
                sql,
                picture.picture,
                picture.pictureId
            )
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t update picture {}", picture)
            throw RepositoryException("REPOSITORY Can`t update picture exception", e)
        }
    }

    override fun getPictureById(pictureId: UUID): Picture? {
        LOGGER.debug("REPOSITORY get picture id by user id {}", pictureId)
        try {
            val sql = "SELECT * FROM picture WHERE pictureId = ?"
            LOGGER.debug("REPOSITORY get picture by id: {} sql: {}", pictureId, sql)
            return jdbcOperations.queryForObject(sql, { resultSet: ResultSet, i: Int ->
                Picture(
                UUID.fromString(resultSet.getString("pictureId")),
                    resultSet.getString("picture"),
                    resultSet.getTimestamp("dateOfSaving").toLocalDateTime()
                )

            }, pictureId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t get picture id by id {}", pictureId)
            throw RepositoryException("REPOSITORY Can`t get picture by id exception", e)
        }
    }

    override fun removePicture(pictureId: UUID): Int {
        LOGGER.debug("REPOSITORY delete picture by id {}", pictureId)
        try {
            val sql = "DELETE FROM picture WHERE pictureId = ?"
            LOGGER.debug("REPOSITORY delete picture params: {} sql: {}", pictureId, sql)
            return jdbcOperations.update(sql, pictureId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t delete picture by id {}", pictureId)
            throw RepositoryException("REPOSITORY can`t delete picture by id exception", e)
        }
    }
}
