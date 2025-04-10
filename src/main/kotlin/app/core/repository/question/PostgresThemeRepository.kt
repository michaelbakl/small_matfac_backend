package ru.baklykov.app.core.repository.question

import app.core.exception.RepositoryException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.RowMapper
import ru.baklykov.app.core.model.question.QuestionTheme
import java.sql.ResultSet
import java.util.*

class PostgresThemeRepository(private val jdbcOperations: JdbcOperations) : IQuestionThemeRepository {

    private val LOGGER: Logger = LoggerFactory.getLogger(PostgresQuestionRepository::class.java)

    private val themeRowMapper = RowMapper { rs: ResultSet, _: Int ->
        QuestionTheme(
            id = rs.getObject("themeId", UUID::class.java),
            path = rs.getString("path"),
            name = rs.getString("name"),
            level = rs.getInt("level")
        )
    }

    override suspend fun saveTheme(theme: QuestionTheme): Int {
        try {
            LOGGER.debug("REPOSITORY create question theme {}", theme)
            val sql =
                "INSERT INTO question_themes (theme_id, path, name, level) \n" +
                        "            VALUES (?, ?, ?, ?)\n" +
                        "            ON CONFLICT (theme_id) DO UPDATE \n" +
                        "            SET path = EXCLUDED.path, name = EXCLUDED.name, level = EXCLUDED.level"
            return jdbcOperations.update(sql, theme.id, theme.path, theme.name, theme.level)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY create question theme by params {} error", theme, e)
            throw RepositoryException("REPOSITORY create question theme exception", e)
        }
    }

    override suspend fun findById(themeId: UUID): QuestionTheme? {
        try {
            LOGGER.debug("REPOSITORY find question theme by id {}", themeId)
            val sql =
                "SELECT * FROM question_themes WHERE theme_id = ?"
            return jdbcOperations.queryForObject(sql, themeRowMapper, themeId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY find question theme by id {} error", themeId, e)
            throw RepositoryException("REPOSITORY find question theme by id exception", e)
        }
    }

    override suspend fun findByQuestionId(questionId: UUID): List<QuestionTheme> {
        try {
            LOGGER.debug("REPOSITORY find question theme by question id {}", questionId)
            val sql =
                "SELECT t.* FROM question_themes t\n" +
                        "            JOIN question_theme_relations r ON t.theme_id = r.theme_id\n" +
                        "            WHERE r.question_id = ?\n" +
                        "            ORDER BY t.path"
            return jdbcOperations.query(sql, themeRowMapper, questionId) ?: emptyList()
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY find question theme by question id {} error", questionId, e)
            throw RepositoryException("REPOSITORY find question theme by question id exception", e)
        }
    }

    override suspend fun searchThemes(query: String, limit: Int): List<QuestionTheme> {
        try {
            LOGGER.debug("REPOSITORY find themes by query {} and limit {}", query, limit)
            val sql =
                "SELECT * FROM question_themes \n" +
                        "            WHERE name ILIKE ? \n" +
                        "            ORDER BY path\n" +
                        "            LIMIT ?"
            return jdbcOperations.query(sql, themeRowMapper, "%$query%", limit) ?: emptyList()
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY find themes by query {} and limit {} error", query, limit, e)
            throw RepositoryException("REPOSITORY find themes by query and limit exception", e)
        }
    }

    override suspend fun findChildThemes(parentThemeId: UUID): List<QuestionTheme> {
        try {
            LOGGER.debug("REPOSITORY find child themes by parent id {}", parentThemeId)
            val parentPath = findById(parentThemeId)?.path ?: return emptyList()
            val sql =
                "SELECT * FROM question_themes \n" +
                        "            WHERE path ~ ?\n" +
                        "            ORDER BY path"
            return jdbcOperations.query(sql, themeRowMapper, "$parentPath.*{1}") ?: emptyList()
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY find child themes by parent id {} error", parentThemeId, e)
            throw RepositoryException("REPOSITORY find child themes by parent id exception", e)
        }
    }

    override suspend fun getThemePath(themeId: UUID): List<QuestionTheme> {
        try {
            LOGGER.debug("REPOSITORY get themes path as list by theme id {}", themeId)
            val theme = findById(themeId) ?: return emptyList()
            val pathParts = theme.path.split(".")
            val paths = pathParts.mapIndexed { i, _ ->
                pathParts.take(i + 1).joinToString(".")
            }
            val sql =
                "SELECT * FROM question_themes \n" +
                        "            WHERE path = ANY(?::ltree[])\n" +
                        "            ORDER BY level"
            return jdbcOperations.query(sql, themeRowMapper, paths.toTypedArray()) ?: emptyList()
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY get themes path as list by theme id {} error", themeId, e)
            throw RepositoryException("REPOSITORY get themes path as list by theme id exception", e)
        }
    }

    override suspend fun addThemeToQuestion(questionId: UUID, themeId: UUID): Int {
        try {
            LOGGER.debug("REPOSITORY add theme to question by id {} {}", questionId, themeId)
            val sql =
                "INSERT INTO question_theme_relations (question_id, theme_id)\n" +
                        "            VALUES (?, ?)\n" +
                        "            ON CONFLICT DO NOTHING"
            return jdbcOperations.update(sql, questionId, themeId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY add theme to question by id {} {} error", questionId, themeId, e)
            throw RepositoryException("REPOSITORY add theme to question by id exception", e)
        }
    }

    override suspend fun removeThemeFromQuestion(questionId: UUID, themeId: UUID): Int {
        try {
            LOGGER.debug("REPOSITORY removes theme from question by id {} {}", questionId, themeId)
            val sql =
                "DELETE FROM question_theme_relations \n" +
                        "            WHERE question_id = ? AND theme_id = ?"
            return jdbcOperations.update(sql, questionId, themeId)
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY removes theme from question by id {} {} error", questionId, themeId, e)
            throw RepositoryException("REPOSITORY removes theme from question by id exception", e)
        }
    }

    override suspend fun getParentThemes(themeId: UUID): List<QuestionTheme> {
        try {
            LOGGER.debug("REPOSITORY get parent themes by theme id {}", themeId)
            val theme = findById(themeId) ?: return emptyList()
            if (theme.level <= 1) return emptyList()
            val sql =
                "SELECT * FROM question_themes \n" +
                        "            WHERE path @> ?::ltree AND level < ?\n" +
                        "            ORDER BY level"
            return jdbcOperations.query(sql, themeRowMapper, theme.path, theme.level) ?: emptyList()
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY get parent themes by theme id {} error", themeId, e)
            throw RepositoryException("REPOSITORY get parent themes by theme id exception", e)
        }
    }

    override suspend fun deleteTheme(themeId: UUID): Boolean {
        try {
            LOGGER.debug("REPOSITORY delete theme by theme id from database {}", themeId)
            val sql =
                "WITH deleted AS (\n" +
                        "                DELETE FROM question_themes \n" +
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

    override suspend fun isDescendant(parentId: UUID, childId: UUID): Boolean {
        try {
            LOGGER.debug("REPOSITORY check if childId is a remnant of parentId {} {}", parentId, childId)
            val sql =
                "SELECT EXISTS (\n" +
                        "                SELECT 1 FROM question_themes parent\n" +
                        "                JOIN question_themes child ON child.path <@ parent.path\n" +
                        "                WHERE parent.theme_id = ? AND child.theme_id = ?\n" +
                        "            )"
            return jdbcOperations.queryForObject(sql, Boolean::class.java, parentId, childId) ?: false
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY checking if childId is a remnant of parentId {} {} error", parentId, childId, e)
            throw RepositoryException("REPOSITORY check if childId is a remnant of parentId exception", e)
        }
    }

    @Cacheable("themePaths", key = "#path")
    override suspend fun findIdByPath(path: String): UUID? {
        try {
            LOGGER.debug("REPOSITORY find theme id by ltree path {}", path)
            return jdbcOperations.query(
                "SELECT theme_id FROM question_themes WHERE path = ? LIMIT 1",
                { rs, _ -> rs.getObject("theme_id", UUID::class.java) },
                path
            ).firstOrNull()
        } catch (e: Exception) {
            LOGGER.error("REPOSITORY find theme id by ltree path {}} error", path, e)
            throw RepositoryException("REPOSITORY find theme id by ltree path exception", e)
        }
    }
}