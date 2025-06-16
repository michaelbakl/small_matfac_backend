package app.core.repository.answer

import app.core.exception.RepositoryException
import app.core.model.game.AnswerSubmission
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
open class PostgresAnswerSubmissionRepository(private val jdbcOperations: JdbcOperations) :
    IAnswerSubmissionRepository {
    private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)

    override fun save(submission: AnswerSubmission): Int {
        LOGGER.debug("REPOSITORY submit answer {}", submission)
        try {
            val sql = """
            INSERT INTO answer_submission (
                submission_id, session_id, question_id, selected_option_id, typed_answer, is_correct, submitted_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

            return jdbcOperations.update(
                sql,
                submission.submissionId,
                submission.sessionId,
                submission.questionId,
                submission.selectedOptionId,
                submission.typedAnswer,
                submission.isCorrect,
                submission.submittedAt
            )
        } catch (e: DataAccessException) {
            LOGGER.error("REPOSITORY error submitting answer {}", submission, e)
            throw RepositoryException("REPOSITORY can not submit answer", e)
        }
    }
}