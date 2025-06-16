package ru.baklykov.app.core.repository.question

import app.core.model.question.QuestionTheme
import java.util.UUID

interface IQuestionThemeRepository {

    /**
     * creates question theme in database
     * @param theme - object of `QuestionTheme` class
     * @return 1 if success, 0 otherwise
     */
    fun saveTheme(theme: QuestionTheme): Int

    /**
     * finds theme by id
     * @param themeId - id of the theme
     * @return object of QuestionTheme class if found, null otherwise
     */
    fun findById(themeId: UUID): QuestionTheme?

    /**
     * finds all themes by the question id
     * @param questionId - id of the question
     * @return list of objects of QuestionTheme Class
     */
    fun findByQuestionId(questionId: UUID): List<QuestionTheme>

    /**
     * hierarchy search of themes with limit
     * @param query - string path of the ltree organized themes
     * @param limit - limit of the search (50 by default)
     * @return list of found themes
     */
    fun searchThemes(query: String, limit: Int = 50): List<QuestionTheme>

    /**
     * finds subthemes by id of the parent theme
     * @param parentThemeId - id of the parent theme
     * @return all found children of the theme or empty list if not found
     */
    fun findChildThemes(parentThemeId: UUID): List<QuestionTheme>

    /**
     * returns path (list of themes) by theme id
     * @param themeId - id of the theme
     * @return path organized as list
     */
    fun getThemePath(themeId: UUID): List<QuestionTheme>

    /**
     * adds theme to question
     * @param questionId - id of the question
     * @param themeId - id of the theme
     * @return 1 if success, 0 otherwise
     */
    fun addThemeToQuestion(questionId: UUID, themeId: UUID): Int

    /**
     * removes theme from the question
     * @param questionId - id of the question
     * @param themeId - id of the theme
     * @return 1 if success, 0 otherwise
     */
    fun removeThemeFromQuestion(questionId: UUID, themeId: UUID): Int

    /**
     * finds parents themes
     * @param themeId - id of the theme
     * @return list of parents themes
     */
    fun getParentThemes(themeId: UUID): List<QuestionTheme>

    /**
     * deletes theme from database
     * @param themeId - id of the theme
     * @return true if success, false otherwise
     */
    fun deleteTheme(themeId: UUID): Boolean

    /**
     * checks if childId is a child of parentId in hierarchy
     * @param parentId - UUID of potential parent
     * @param childId - UUID of child
     * @return true if child is a remnant of a parent (or coincide with him)
     */
    fun isDescendant(parentId: UUID, childId: UUID): Boolean

    /**
     * finds the id of theme by path
     * @param path - ltree path of themes
     * @return uuid of the theme if found
     */
    fun findIdByPath(path: String): UUID?
}
