package app.core.repository.admin

import app.core.exception.RepositoryException
import app.core.model.person.RegistrationRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.Timestamp
import java.util.UUID

@Repository
open class PostgresRequestsRepository(
    private val jdbcOperations: JdbcOperations
) : IRequestsRepository {
    private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)

    private val rowMapper = RowMapper { rs: ResultSet, _: Int ->
        RegistrationRequest(
            id = UUID.fromString(rs.getString("id")),
            email = rs.getString("email"),
            passwordHash = rs.getString("password_hash"),
            surname = rs.getString("surname"),
            name = rs.getString("name"),
            middleName = rs.getString("middle_name"),
            requestedRole = rs.getString("requested_role"),
            status = rs.getString("status"),
            submittedAt = rs.getTimestamp("submitted_at").toInstant(),
            reviewedAt = rs.getTimestamp("reviewed_at")?.toInstant(),
            reviewerId = rs.getObject("reviewer_id", UUID::class.java)
        )
    }

    override fun save(request: RegistrationRequest): Int {
        try {
            LOGGER.debug("REPOSITORY save registration request {}", request)
            return jdbcOperations.update(
                """
            INSERT INTO registration_requests (
                id, email, password_hash, surname, name, middle_name, requested_role, status, submitted_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
                request.id,
                request.email,
                request.passwordHash,
                request.surname,
                request.name,
                request.middleName,
                request.requestedRole,
                request.status,
                Timestamp.from(request.submittedAt)
            )
        } catch (e: DataAccessException) {
            LOGGER.error("REPOSITORY save registration request exception {}", request)
            throw RepositoryException("REPOSITORY save registration request exception", e)
        }
    }

    override fun findById(id: UUID): RegistrationRequest? {
        try {
            LOGGER.debug("REPOSITORY find registration request by id {}", id)
            val sql = "SELECT * FROM registration_requests WHERE id = ?"
            return jdbcOperations.query(sql, rowMapper, id).firstOrNull()
        } catch (e: DataAccessException) {
            LOGGER.error("REPOSITORY find registration request by id exception {}", id)
            throw RepositoryException("REPOSITORY find registration request by id exception", e)
        }
    }

    override fun existsByEmail(email: String): Boolean {
        try {
            LOGGER.debug("REPOSITORY check registration request exists by email {}", email)
            val sql = "SELECT COUNT(*) FROM registration_requests WHERE email = ?"
            val count = jdbcOperations.queryForObject(sql, Long::class.java, email)
            return count != null && count > 0
        } catch (e: DataAccessException) {
            LOGGER.error("REPOSITORY check registration request exists by email exception {}", email)
            throw RepositoryException("REPOSITORY check registration request exists by email exception", e)
        }
    }

    override fun updateStatus(id: UUID, status: String, reviewerId: UUID) {
        try {
            LOGGER.debug("REPOSITORY update status in registration request {} , {} , {}", id, status, reviewerId)
            val sql = """
            UPDATE registration_requests
            SET status = ?, reviewer_id = ?, reviewed_at = ?
            WHERE id = ?
        """.trimIndent()
            jdbcOperations.update(sql, status, reviewerId, Timestamp.from(java.time.Instant.now()), id)
        } catch (e: DataAccessException) {
            LOGGER.error("REPOSITORY update status in registration request exception {} , {} , {}", id, status, reviewerId)
            throw RepositoryException("REPOSITORY update status in registration request exception", e)
        }
    }

    override fun findAllPending(): List<RegistrationRequest> {
        try {
            LOGGER.debug("REPOSITORY find all pending requests")
            val sql = "SELECT * FROM registration_requests WHERE status = 'PENDING'"
            return jdbcOperations.query(sql, rowMapper)
        } catch (e: DataAccessException) {
            LOGGER.error("REPOSITORY find all pending requests exception")
            throw RepositoryException("REPOSITORY find all pending requests exception", e)
        }
    }
}
