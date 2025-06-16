package ru.baklykov.app.core.service.question

import app.core.model.question.QuestionTheme
import app.web.model.response.theme.ThemeResponse
import java.util.UUID

interface IThemeService {

    /**
     * creates theme by params
     * @param name - name of the theme
     * @param parentThemeId - id of the parent which the new theme inherits from
     * @return added theme object class QuestionTheme
     */
    fun createTheme(name: String, parentThemeId: UUID? = null): QuestionTheme

    /**
     * updates theme name by theme id
     * @param themeId - id of the theme
     * @param newName - new name to update
     * @return updated theme object class QuestionTheme
     */
    fun updateTheme(themeId: UUID, newName: String): QuestionTheme

    /**
     * finds theme by theme id
     * @param themeId - id of the theme to find
     * @return found theme object class QuestionTheme
     */
    fun getTheme(themeId: UUID): QuestionTheme

    /**
     * deletes theme from the server by id
     * @param themeId - id of the theme to delete
     * @return true if success, false otherwise
     */
    fun deleteTheme(themeId: UUID): Boolean


    /**
     *
     */
    fun getThemeHierarchy(rootThemeId: UUID? = null): List<QuestionTheme>
    fun getParentThemes(themeId: UUID): List<QuestionTheme>
    fun getChildThemes(themeId: UUID): List<QuestionTheme>


    fun addThemeToQuestion(questionId: UUID, themeId: UUID)
    fun removeThemeFromQuestion(questionId: UUID, themeId: UUID)
    fun getQuestionThemes(questionId: UUID): List<QuestionTheme>
    fun setQuestionThemes(questionId: UUID, themeIds: List<UUID>)


    /**
     *
     */
    fun searchThemes(query: String, limit: Int = 20): List<QuestionTheme>


    /**
     * validates theme path
     *
     */
    fun validateThemePath(themeId: UUID, newParentId: UUID?): Boolean

    /**
     * converts theme to response
     * @param theme - model class
     * @return response of theme
     */
    fun convertToResponse(theme: QuestionTheme): ThemeResponse

}