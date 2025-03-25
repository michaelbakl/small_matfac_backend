package ru.baklykov.app.core.repository.question

import app.core.exception.RepositoryException
import app.core.repository.student.PostgresStudentRepository
import app.core.util.SqlQueryBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.RowMapper
import ru.baklykov.app.core.model.Question
import ru.baklykov.app.core.model.person.Student
import java.sql.ResultSet
import java.sql.Timestamp
import java.util.*

open class PostgresQuestionRepository(private val jdbcOperations: JdbcOperations) : IQuestionRepository {
    private val LOGGER: Logger = LoggerFactory.getLogger(PostgresQuestionRepository::class.java)

    override fun addQuestion(question: Question): Int {
        LOGGER.debug("REPOSITORY add question {}", question)
        try {
            val sql =
                "INSERT INTO question (questionId, ownerId, title, description)" +
                        " VALUES (?, ?, ?, ?)"
            LOGGER.debug("REPOSITORY add question params: {} sql: {}", question, sql)
            return jdbcOperations.update(
                sql,
                question.questionId,
                question.ownerId,
                question.title,
                question.description
            )
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t add question {}", question)
            throw RepositoryException("REPOSITORY Can`t add question exception", e)
        }
    }

    override fun updateQuestion(question: Question): Int {
        LOGGER.debug("REPOSITORY update question {}", question)
        try {
            val sql =
                "UPDATE question SET ownerId=?, title=?, description=?" +
                        " WHERE questionId=?"
            LOGGER.debug("REPOSITORY update question params: {} sql: {}", question, sql)
            return jdbcOperations.update(
                sql,
                question.questionId,
                question.ownerId,
                question.title,
                question.description
            )
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t update question {}", question)
            throw RepositoryException("REPOSITORY Can`t update question exception", e)
        }
    }

    override fun deleteQuestionById(questionId: UUID): Int {
        LOGGER.debug("REPOSITORY remove question {}", questionId)
        try {
            val sql = "DELETE FROM question WHERE questionId=?"
            LOGGER.debug("REPOSITORY delete question by id: {} sql: {}", questionId, sql)
            return jdbcOperations.update(sql, questionId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t delete question by id {}", questionId)
            throw RepositoryException("REPOSITORY Can`t delete question by id exception", e)
        }
    }

    override fun getQuestionById(questionId: UUID): Question {
        TODO("Not yet implemented")
    }

    override fun getQuestionWithParams(
        questionId: UUID?,
        ownerId: UUID?,
        title: String?,
        description: String?,
        themes: List<UUID>?
    ): List<Question> {
        LOGGER.debug("REPOSITORY get questions with params {}, {}, {}, {}", questionId, title, description, themes)
        try {
            val sql = SqlQueryBuilder()
                .select("*")
                .from("question")
                .where("questionId", questionId?.toString())
                .where("ownerId", ownerId?.toString())
                .where("title", title)
                .where("description", description)
                //TODO: add filter by themes
                .build()

            val rowMapper: RowMapper<Question> = RowMapper<Question> { resultSet: ResultSet, rowIndex: Int ->
                Question(
                    UUID.fromString(resultSet.getString("questionId")),
                    UUID.fromString(resultSet.getString("ownerId")),
                    resultSet.getString("title"),
                    resultSet.getString("description"),
                    resultSet.getString("email"),
                )
            }
            LOGGER.debug("REPOSITORY get questions by params: {}, {}, {}, {} sql: {}",
                questionId, title, description, themes, sql
            )
            return jdbcOperations.query(sql, rowMapper)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY can`t get students by filter: {}", filter)
            throw RepositoryException("REPOSITORY can`t get students by filter exception", e)
        }
    }

    override fun addAnswerToQuestion(questionId: UUID, answer: Pair<UUID, Pair<Boolean, Int>>): Int {
        TODO("Not yet implemented")
    }

    override fun removeAnswerFromQuestion(questionId: UUID, answerId: UUID): Int {
        TODO("Not yet implemented")
    }
}