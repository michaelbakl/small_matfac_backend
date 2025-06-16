package app.core.repository.question

import app.core.exception.NotFoundException
import app.core.exception.RepositoryException
import app.core.util.SqlQueryBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import app.core.model.question.Question
import app.web.model.dto.answer.AnswerDto
import org.springframework.dao.DataAccessException
import org.springframework.dao.EmptyResultDataAccessException
import ru.baklykov.app.core.repository.question.IQuestionRepository
import java.sql.ResultSet
import java.util.*

@Repository
open class PostgresQuestionRepository(private val jdbcOperations: JdbcOperations) : IQuestionRepository {

    private val LOGGER: Logger = LoggerFactory.getLogger(PostgresQuestionRepository::class.java)

    private val questionRowMapper = RowMapper { rs: ResultSet, _: Int ->
        Question(
            questionId = rs.getObject("questionId", UUID::class.java),
            ownerId = rs.getObject("ownerId", UUID::class.java),
            title = rs.getString("title"),
            type = rs.getString("type"),
            //pictures = getQuestionsPicturesIds(rs.getObject("questionId", UUID::class.java)),
            pictures = emptyList(),
            description = rs.getString("description"),
            answers = getQuestionsAnswers(rs.getObject("questionId", UUID::class.java))
        )
    }

    private val answerDtoMapper = RowMapper {rs: ResultSet, _: Int ->
        AnswerDto(
            rs.getObject("answerId", UUID::class.java),
            rs.getString("description"),
            rs.getBoolean("isright"),
            rs.getInt("points")
        )
    }

    override fun addQuestion(question: Question): Int {
        LOGGER.debug("REPOSITORY add question {}", question)
        try {
            val sql = "INSERT INTO question (questionId, ownerId, type, title, description) VALUES (?, ?, ?::question_type, ?, ?)"
            LOGGER.debug("REPOSITORY add question params: {} sql: {}", question, sql)
            return jdbcOperations.update(
                sql,
                question.questionId,
                question.ownerId,
                question.type,
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

    override fun countByTitleContaining(query: String): Long {
        LOGGER.debug("REPOSITORY count by title containing {}", query)
        try {

            val sql = "SELECT COUNT(*) FROM question WHERE title ILIKE ?"

            LOGGER.debug("REPOSITORY count by title containing {} sql {}", query, sql)
            return jdbcOperations.queryForObject(sql, Long::class.java, "%$query%") ?: 0
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY error counting by title containing {}", query)
            throw RepositoryException("REPOSITORY count by title containing exception", e)
        }
    }

    override fun countByTeacherId(teacherId: UUID): Long {
        LOGGER.debug("REPOSITORY count by teacher id  {}", teacherId)
        try {

            val sql = "SELECT COUNT(*) FROM question q\n" +
                    "            JOIN question_teacher qt ON q.question_id = qt.question_id\n" +
                    "            WHERE qt.teacher_id = ?"

            LOGGER.debug("REPOSITORY count by teacher id {} sql {}", teacherId, sql)
            return jdbcOperations.queryForObject(sql, Long::class.java, teacherId) ?: 0
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY error counting by teacher id  {}", teacherId)
            throw RepositoryException("REPOSITORY count by teacher id exception", e)
        }
    }

    override fun countByThemeIds(themeIds: List<UUID>): Long {
        LOGGER.debug("REPOSITORY count by theme ids  {}", themeIds)
        try {

            val sql = "SELECT COUNT(DISTINCT q.question_id) FROM question q\n" +
                    "            JOIN question_theme_relations qtr ON q.question_id = qtr.question_id\n" +
                    "            WHERE qtr.theme_id = ANY(?)"

            LOGGER.debug("REPOSITORY count by theme ids {} sql {}", themeIds, sql)
            return jdbcOperations.queryForObject(sql, Long::class.java, themeIds.toTypedArray()) ?: 0
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY error counting by theme ids  {}", themeIds)
            throw RepositoryException("REPOSITORY count by theme ids exception", e)
        }
    }

    override fun existsByIdAndTeacherId(questionId: UUID, teacherId: UUID): Boolean {
        LOGGER.debug("REPOSITORY check exists by id and teacher id  {} {}", questionId, teacherId)
        try {

            val sql = "SELECT EXISTS(\n" +
                    "                SELECT 1 FROM question_teacher \n" +
                    "                WHERE question_id = ? AND teacher_id = ?\n" +
                    "            )"

            LOGGER.debug("REPOSITORY check exists by id and teacher id  {} {} sql {}", questionId, teacherId, sql)
            return jdbcOperations.queryForObject(sql, Boolean::class.java, questionId, teacherId) ?: false
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY error check exists by id and teacher id  {} {}", questionId, teacherId)
            throw RepositoryException("REPOSITORY check exists by id and teacher id exception", e)
        }
    }

    override fun findByThemeId(themeId: UUID, pageable: Pageable): List<Question> {
        LOGGER.debug("REPOSITORY find questions by theme id pageable {} {}", themeId, pageable)
        try {

            val sql = "SELECT q.* FROM question q\n" +
                    "            JOIN question_theme_relations qtr ON q.question_id = qtr.question_id\n" +
                    "            WHERE qtr.theme_id = ?\n" +
                    "            ORDER BY q.created_at DESC\n" +
                    "            LIMIT ? OFFSET ?"

            LOGGER.debug("REPOSITORY find by theme id pageable {} {} sql {}", themeId, pageable, sql)

            return jdbcOperations.query(sql, questionRowMapper, themeId, pageable.pageSize, pageable.offset) ?: emptyList()
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY error finding questions by theme id pageable {} {}", themeId, pageable)
            throw RepositoryException("REPOSITORY find questions by theme pageable id exception", e)
        }
    }

    override fun findByThemeId(themeId: UUID): List<Question> {
        LOGGER.debug("REPOSITORY find questions by theme id {}", themeId)
        try {

            val sql = "SELECT q.* FROM question q\n" +
                    "            JOIN question_theme_relations qtr ON q.question_id = qtr.question_id\n" +
                    "            WHERE qtr.theme_id = ?\n" +
                    "            ORDER BY q.created_at DESC"

            LOGGER.debug("REPOSITORY find by theme id {} sql {}", themeId, sql)

            return jdbcOperations.query(sql, questionRowMapper, themeId) ?: emptyList()
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY error finding questions by theme id  {}", themeId)
            throw RepositoryException("REPOSITORY find questions by theme id exception", e)
        }
    }

    override fun findByTitleContaining(query: String, pageable: Pageable): List<Question> {
        LOGGER.debug("REPOSITORY find questions by title with mask paginated {} {}", query, pageable)
        try {

            val sql = "SELECT * FROM question \n" +
                    "            WHERE title ILIKE ? \n" +
                    "            ORDER BY title DESC\n" +
                    "            LIMIT ? OFFSET ?"

            LOGGER.debug("REPOSITORY find questions by title with mask paginated {} {} sql {}", query, pageable, sql)

            return jdbcOperations.query(sql, questionRowMapper, "%$query%", pageable.pageSize, pageable.offset)
                ?: emptyList()
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY error finding questions by title with mask paginated {} {}", query, pageable)
            throw RepositoryException("REPOSITORY find questions by title with mask paginated exception", e)
        }
    }

    override fun findByTitleContaining(query: String): List<Question> {
        LOGGER.debug("REPOSITORY find questions by title with mask {}", query)
        try {

            val sql = "SELECT * FROM question \n" +
                    "            WHERE title ILIKE ? \n" +
                    "            ORDER BY title DESC"

            LOGGER.debug("REPOSITORY find questions by title with mask {} sql {}", query, sql)

            return jdbcOperations.query(sql, questionRowMapper, "%$query%") ?: emptyList()
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY error finding questions by title with mask {}", query)
            throw RepositoryException("REPOSITORY find questions by title with mask exception", e)
        }
    }

    override fun findByTitleAndDescription(title: String, description: String): Question? {
        LOGGER.debug("REPOSITORY find question by title and description: {}, {}", title, description)
        try {
            val sql = "SELECT * FROM question WHERE title = ? AND description = ? LIMIT 1"
            return jdbcOperations.queryForObject(sql, questionRowMapper, title, description)
        } catch (e: EmptyResultDataAccessException) {
            LOGGER.debug("REPOSITORY not found question by title and description: {}, {}", title, description)
            return null
        }
        catch (e: DataAccessException) {
            LOGGER.error("REPOSITORY Error finding question by title and description: {}, {}", title, description)
            throw RepositoryException("REPOSITORY Error finding question by title and description", e)
        }
    }


    private fun getQuestionsPicturesIds(questionId: UUID): List<UUID> {
        val sql = "SELECT * FROM question_picture WHERE questionId=?"
        return jdbcOperations.query(sql, { rs, _ -> rs.getObject("pictureId", UUID::class.java) }, questionId)
    }

    private fun getQuestionsAnswers(questionId: UUID): List<AnswerDto> {
        try {
            val sql =
                "SELECT * FROM answer INNER JOIN question_answer ON answer.answerId=question_answer.answerId WHERE question_answer.questionId=?"
            return jdbcOperations.query(sql, answerDtoMapper, questionId)
        } catch (e: Exception){
            return emptyList()
        }
    }

}
