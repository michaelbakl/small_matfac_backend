package app.core.repository.role

import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(rollbackFor = [DataAccessException::class])
open class PostgresRoleRepository(private val jdbcOperations: JdbcOperations): IRoleRepository {

    override fun addNewRole(roleName: String, requiresApproval: Boolean): Int {
        val sql = "INSERT INTO roles (name, requires_approval) VALUES (?, ?)"
        return jdbcOperations.update(sql, roleName, requiresApproval)
    }

    override fun deleteRole(roleName: String): Int {
        val sql = "DELETE FROM roles WHERE name = ?"
        return jdbcOperations.update(sql, roleName)
    }

    override fun updateRoleApproval(roleName: String, requiresApproval: Boolean): Int {
        val sql = "UPDATE roles SET requires_approval = ? WHERE name = ?"
        return jdbcOperations.update(sql, requiresApproval, roleName)
    }

    override fun requiresApproval(roleName: String): Boolean {
        val sql = "SELECT requires_approval FROM roles WHERE name = ?"
        return jdbcOperations.queryForObject(sql, Boolean::class.java, roleName)
            ?: throw IllegalArgumentException("Role not found: $roleName")
    }

    override fun getAllRoles(): List<String> {
        val sql = "SELECT name FROM roles"
        return jdbcOperations.query(sql) { rs, _ -> rs.getString("name") }
    }

}
