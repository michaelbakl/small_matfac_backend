package ru.baklykov.app.core.repository.question

import app.core.exception.NotFoundException
import app.core.exception.RepositoryException
import app.core.util.SqlQueryBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.RowMapper
import org.springframework.transaction.annotation.Transactional
import ru.baklykov.app.core.model.question.Question
import ru.baklykov.app.web.model.dto.answer.AnswerDto
import java.sql.ResultSet
import java.util.*

open class PostgresQuestionRepository(private val jdbcOperations: JdbcOperations) : IQuestionRepository {

    private val LOGGER: Logger = LoggerFactory.getLogger(PostgresQuestionRepository::class.java)

    private val questionRowMapper = RowMapper { rs: ResultSet, _: Int ->
        Question(
            questionId = rs.getObject("questionId", UUID::class.java),
            ownerId = rs.getObject("ownerId", UUID::class.java),
            title = rs.getString("title"),
            type = rs.getString("question_type"),
            pictures = getQuestionsPicturesIds(rs.getObject("questionId", UUID::class.java)),
            description = rs.getString("description"),
            answers = getQuestionsAnswers(rs.getObject("questionId", UUID::class.java))
        )
    }

    private val answerDtoMapper = RowMapper {rs: ResultSet, _: Int ->
        AnswerDto(
            rs.getObject("answerId", UUID::class.java),
            rs.getString("description"),
            rs.getBoolean("isRight"),
            rs.getInt("points")
        )
    }

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

    override fun getQuestionById(questionId: UUID): Question? {
        LOGGER.debug("REPOSITORY get question by id {}", questionId)
        try {
            val sql = "SELECT * FROM question WHERE questionId=?"
            LOGGER.debug("REPOSITORY get question by id: {} sql: {}", questionId, sql)

            return jdbcOperations.queryForObject(sql, questionRowMapper, questionId) ?: throw NotFoundException("")
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY Can`t get question by id {}", questionId)
            throw RepositoryException("REPOSITORY Can`t get question by id exception", e)
        }
    }

    // TODO: change the method
    override fun getQuestionWithParams(
        questionId: UUID?,
        ownerId: UUID?,
        title: String?,
        description: String?,
        themes: List<UUID>?,
        themesStr: String?
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

            LOGGER.debug(
                "REPOSITORY get questions by params: {}, {}, {}, {} sql: {}",
                questionId, title, description, themes, sql
            )
            return jdbcOperations.query(sql, questionRowMapper)
        } catch (e: Exception) {
            LOGGER.error(
                "REPOSITORY can`t get students by filter: {}, {}, {}, {}",
                questionId, title, description, themes
            )
            throw RepositoryException("REPOSITORY can`t get students by filter exception", e)
        }
    }

    @Transactional
    override fun addAnswerToQuestion(questionId: UUID, answer: Pair<UUID, Pair<Boolean, Int>>): Int {
        LOGGER.debug("REPOSITORY add answer to question by id {} {}", questionId, answer)
        try {
            val sql = "INSERT INTO question_answer (questionId, answerId, isRight, points) VALUES (?, ?, ?, ?)"
            LOGGER.debug("REPOSITORY add answer to question by id {} {} sql {}", questionId, answer, sql)
            return jdbcOperations.update(sql, questionId, answer.first, answer.second.first, answer.second.second)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY error add answer to question by id {} {}", questionId, answer)
            throw RepositoryException("REPOSITORY add answer to question by id exception", e)
        }
    }

    @Transactional
    override fun removeAnswerFromQuestion(questionId: UUID, answerId: UUID): Int {
        LOGGER.debug("REPOSITORY remove answer from question by id {} {}", questionId, answerId)
        try {
            val sql = "DELETE FROM question_answer WHERE questionId=? AND answerId=?"
            LOGGER.debug("REPOSITORY remove answer from question by id {} {} sql {}", questionId, answerId, sql)
            return jdbcOperations.update(sql, questionId, answerId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY error remove answer from question by id {} {}", questionId, answerId)
            throw RepositoryException("REPOSITORY remove answer from question by id exception", e)
        }
    }

    override fun existsById(questionId: UUID): Boolean {
        LOGGER.debug("REPOSITORY check question exists by id {}", questionId)
        try {
            val sql = "SELECT EXISTS(SELECT 1 FROM question WHERE questionId=? LIMIT 1)"
            LOGGER.debug("REPOSITORY check question exists by id {} sql {}", questionId, sql)
            return jdbcOperations.queryForObject(sql, Boolean::class.java, questionId) ?: false
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY error checking question exists by id {}", questionId)
            throw RepositoryException("REPOSITORY check question exists by id exception", e)
        }
    }

    private fun getQuestionsPicturesIds(questionId: UUID): List<UUID> {
        val sql = "SELECT * FROM question_picture WHERE questionId=?"
        return jdbcOperations.query(sql, { rs, _ -> rs.getObject("pictureId", UUID::class.java) }, questionId)
    }

    private fun getQuestionsAnswers(questionId: UUID): List<AnswerDto> {
        val sql = "SELECT * FROM answer JOIN question_answer ON answer.answerId=question_answer.answerId WHERE questionId=?"
        return jdbcOperations.query(sql, answerDtoMapper)
    }

}
