package ru.baklykov.app.core.repository.question

import app.core.exception.RepositoryException
import app.core.repository.question.PostgresQuestionRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import app.core.model.question.QuestionTheme
import java.sql.ResultSet
import java.util.*

@Transactional
@Repository
open class PostgresThemeRepository(private val jdbcOperations: JdbcOperations) : IQuestionThemeRepository {

    private val LOGGER: Logger = LoggerFactory.getLogger(PostgresQuestionRepository::class.java)

    private val themeRowMapper = RowMapper { rs: ResultSet, _: Int ->
        QuestionTheme(
            id = rs.getObject("themeId", UUID::class.java),
            path = rs.getString("path"),
            name = rs.getString("name"),
            level = rs.getInt("level")
        )
    }

    override fun saveTheme(theme: QuestionTheme): Int {
        try {
            LOGGER.debug("REPOSITORY create question theme {}", theme)
            val sql =
                "INSERT INTO theme (themeId, path, name, level) \n" +
                        "            VALUES (?, ?::ltree, ?, ?)\n" +
                        "            ON CONFLICT (themeId) DO UPDATE \n" +
                        "            SET path = EXCLUDED.path, name = EXCLUDED.name, level = EXCLUDED.level"
            return jdbcOperations.update(sql, theme.id, theme.path, theme.name, theme.level)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY create question theme by params {} error", theme, e)
            throw RepositoryException("REPOSITORY create question theme exception", e)
        }
    }

    override fun findById(themeId: UUID): QuestionTheme? {
        try {
            LOGGER.debug("REPOSITORY find question theme by id {}", themeId)
            val sql =
                "SELECT * FROM theme WHERE themeId = ?"
            return jdbcOperations.queryForObject(sql, themeRowMapper, themeId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY find question theme by id {} error", themeId, e)
            throw RepositoryException("REPOSITORY find question theme by id exception", e)
        }
    }

    override fun findByQuestionId(questionId: UUID): List<QuestionTheme> {
        try {
            LOGGER.debug("REPOSITORY find question theme by question id {}", questionId)
            val sql =
                "SELECT t.* FROM theme t\n" +
                        "            JOIN question_theme_relations r ON t.themeId = r.themeId\n" +
                        "            WHERE r.questionId = ?\n" +
                        "            ORDER BY t.path"
            return jdbcOperations.query(sql, themeRowMapper, questionId) ?: emptyList()
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY find question theme by question id {} error", questionId, e)
            throw RepositoryException("REPOSITORY find question theme by question id exception", e)
        }
    }

    override fun searchThemes(query: String, limit: Int): List<QuestionTheme> {
        try {
            LOGGER.debug("REPOSITORY find themes by query {} and limit {}", query, limit)
            val sql =
                "SELECT * FROM theme \n" +
                        "            WHERE name ILIKE ? \n" +
                        "            ORDER BY path\n" +
                        "            LIMIT ?"
            return jdbcOperations.query(sql, themeRowMapper, "%$query%", limit) ?: emptyList()
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY find themes by query {} and limit {} error", query, limit, e)
            throw RepositoryException("REPOSITORY find themes by query and limit exception", e)
        }
    }

    override fun findChildThemes(parentThemeId: UUID): List<QuestionTheme> {
        return try {
            LOGGER.debug("REPOSITORY find child themes by parent id {}", parentThemeId)

            val parentPath = findById(parentThemeId)?.takeIf { it.path.isNotBlank() }?.path
                ?: return emptyList<QuestionTheme>().also {
                    LOGGER.debug("Parent theme not found or has empty path: {}", parentThemeId)
                }

            val lqueryPattern = if (parentPath.contains("*")) {
                LOGGER.warn("Potential unsafe ltree pattern in parent path: {}", parentPath)
                "${parentPath.replace("*", "")}.*{1}"
            } else {
                "$parentPath.*{1}"
            }

            val sql = """
            SELECT * FROM theme 
            WHERE path ~ ?::lquery
            AND NOT path ~ ?::lquery
            ORDER BY path
            """

            jdbcOperations.query(
                sql,
                themeRowMapper,
                lqueryPattern,
                parentPath
            ) ?: emptyList()

        } catch (e: DataAccessException) {
            LOGGER.error("Database error while finding child themes for parent: {}", parentThemeId, e)
            throw RepositoryException("Failed to find child themes", e)
        } catch (e: Exception) {
            LOGGER.error("Unexpected error while finding child themes for parent: {}", parentThemeId, e)
            throw RepositoryException("Unexpected error occurred", e)
        }
    }

    override fun getThemePath(themeId: UUID): List<QuestionTheme> {
        try {
            LOGGER.debug("REPOSITORY get themes path as list by theme id {}", themeId)
            val theme = findById(themeId) ?: return emptyList()
            val pathParts = theme.path.split(".")
            val paths = pathParts.mapIndexed { i, _ ->
                pathParts.take(i + 1).joinToString(".")
            }
            val sql =
                "SELECT * FROM theme \n" +
                        "            WHERE path = ANY(?::ltree[])\n" +
                        "            ORDER BY level"
            return jdbcOperations.query(sql, themeRowMapper, paths.toTypedArray()) ?: emptyList()
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY get themes path as list by theme id {} error", themeId, e)
            throw RepositoryException("REPOSITORY get themes path as list by theme id exception", e)
        }
    }

    override fun addThemeToQuestion(questionId: UUID, themeId: UUID): Int {
        try {
            LOGGER.debug("REPOSITORY add theme to question by id {} {}", questionId, themeId)
            val sql =
                "INSERT INTO question_theme_relations (questionId, themeId)\n" +
                        "            VALUES (?, ?)\n" +
                        "            ON CONFLICT DO NOTHING"
            return jdbcOperations.update(sql, questionId, themeId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY add theme to question by id {} {} error", questionId, themeId, e)
            throw RepositoryException("REPOSITORY add theme to question by id exception", e)
        }
    }

    override fun removeThemeFromQuestion(questionId: UUID, themeId: UUID): Int {
        try {
            LOGGER.debug("REPOSITORY removes theme from question by id {} {}", questionId, themeId)
            val sql =
                "DELETE FROM question_theme_relations \n" +
                        "            WHERE questionId = ? AND themeId = ?"
            return jdbcOperations.update(sql, questionId, themeId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY removes theme from question by id {} {} error", questionId, themeId, e)
            throw RepositoryException("REPOSITORY removes theme from question by id exception", e)
        }
    }

    override fun getParentThemes(themeId: UUID): List<QuestionTheme> {
        try {
            LOGGER.debug("REPOSITORY get parent themes by theme id {}", themeId)
            val theme = findById(themeId) ?: return emptyList()
            if (theme.level <= 1) return emptyList()
            val sql =
                "SELECT * FROM theme \n" +
                        "            WHERE path @> ?::ltree AND level < ?\n" +
                        "            ORDER BY level"
            return jdbcOperations.query(sql, themeRowMapper, theme.path, theme.level) ?: emptyList()
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY get parent themes by theme id {} error", themeId, e)
            throw RepositoryException("REPOSITORY get parent themes by theme id exception", e)
        }
    }

    override fun deleteTheme(themeId: UUID): Boolean {
        try {
            LOGGER.debug("REPOSITORY delete theme by theme id from database {}", themeId)
            val sql =
                "WITH deleted AS (\n" +
                        "                DELETE FROM theme \n" +
                        "                WHERE themeId = ? \n" +
                        "                AND NOT EXISTS (\n" +
                        "                    SELECT 1 FROM question_theme_relations \n" +
                        "                    WHERE themeId = ?\n" +
                        "                )\n" +
                        "                RETURNING 1\n" +
                        "            ) SELECT COUNT(*) > 0 FROM deleted"
            return jdbcOperations.queryForObject(sql, Boolean::class.java, themeId, themeId) ?: false
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY delete theme by theme id {} error", themeId, e)
            throw RepositoryException("REPOSITORY get theme by theme id exception", e)
        }
    }

    override fun isDescendant(parentId: UUID, childId: UUID): Boolean {
        try {
            LOGGER.debug("REPOSITORY check if childId is a remnant of parentId {} {}", parentId, childId)
            val sql =
                "SELECT EXISTS (\n" +
                        "                SELECT 1 FROM theme parent\n" +
                        "                JOIN theme child ON child.path <@ parent.path\n" +
                        "                WHERE parent.themeId = ? AND child.themeId = ?\n" +
                        "            )"
            return jdbcOperations.queryForObject(sql, Boolean::class.java, parentId, childId) ?: false
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY checking if childId is a remnant of parentId {} {} error", parentId, childId, e)
            throw RepositoryException("REPOSITORY check if childId is a remnant of parentId exception", e)
        }
    }

    @Cacheable("themePaths", key = "#path")
    override fun findIdByPath(path: String): UUID? {
        try {
            LOGGER.debug("REPOSITORY find theme id by ltree path {}", path)
            return jdbcOperations.query(
                "SELECT themeId FROM theme WHERE path ~ ?::lquery LIMIT 1",
                { rs, _ -> rs.getObject("themeId", UUID::class.java) },
                path
            ).firstOrNull()
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY find theme id by ltree path {} error", path, e)
            throw RepositoryException("REPOSITORY find theme id by ltree path exception", e)
        }
    }
}