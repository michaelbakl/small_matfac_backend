package ru.baklykov.app.core.service.question

import org.springframework.data.domain.Page
import ru.baklykov.app.web.model.request.question.CreateQuestionRequest
import ru.baklykov.app.web.model.request.question.UpdateQuestionRequest
import ru.baklykov.app.web.model.response.question.GetQuestionInfoResponse
import ru.baklykov.app.web.model.response.theme.ThemeResponse
import java.util.*


/**
 * Service for managing questions and their relationships with themes.
 * Provides CRUD operations, search capabilities, and theme management.
 */
interface IQuestionService {
    /**
     * Creates a new question with the provided details.
     *
     * @param request the question creation request containing title, description,
     * answers, and optional theme associations
     * @return the created question with generated ID
     * @throws IllegalArgumentException if request validation fails
     */
    suspend fun createQuestion(ownerId: String, request: CreateQuestionRequest): GetQuestionInfoResponse?

    /**
     * Retrieves a question by its unique identifier.
     *
     * @param questionId the UUID of the question to retrieve
     * @return the found question
     * @throws QuestionNotFoundException if no question exists with the given ID
     */
    fun getQuestion(questionId: UUID): GetQuestionInfoResponse?

    /**
     * Updates an existing question.
     *
     * @param questionId the UUID of the question to update
     * @param request the update request containing new question details
     * @return the updated question
     * @throws QuestionNotFoundException if no question exists with the given ID
     * @throws IllegalArgumentException if request validation fails
     */
    fun updateQuestion(questionId: UUID, request: UpdateQuestionRequest): GetQuestionInfoResponse?

    /**
     * Deletes a question by its ID. Also removes all theme associations.
     *
     * @param questionId the UUID of the question to delete
     * @throws QuestionNotFoundException if no question exists with the given ID
     */
    fun deleteQuestion(questionId: UUID)

    /**
     * Associates a theme with a question.
     *
     * @param questionId the UUID of the question
     * @param themeId the UUID of the theme to associate
     * @throws QuestionNotFoundException if no question exists with the given ID
     * @throws ThemeNotFoundException if no theme exists with the given ID
     * @throws IllegalStateException if association already exists
     */
    fun addThemeToQuestion(questionId: UUID, themeId: UUID)

    /**
     * Removes a theme association from a question.
     *
     * @param questionId the UUID of the question
     * @param themeId the UUID of the theme to remove
     * @throws QuestionNotFoundException if no question exists with the given ID
     * @throws ThemeNotFoundException if no theme exists with the given ID
     */
    fun removeThemeFromQuestion(questionId: UUID, themeId: UUID)

    /**
     * Replaces all theme associations for a question.
     *
     * @param questionId the UUID of the question
     * @param themeIds the list of theme UUIDs to associate
     * @throws QuestionNotFoundException if no question exists with the given ID
     * @throws ThemeNotFoundException if any theme doesn't exist
     */
    fun setQuestionThemes(questionId: UUID, themeIds: List<UUID>)

    /**
     * Retrieves all themes associated with a question.
     *
     * @param questionId the UUID of the question
     * @return list of associated themes (empty if none)
     * @throws QuestionNotFoundException if no question exists with the given ID
     */
    fun getQuestionThemes(questionId: UUID): List<ThemeResponse?>?

    /**
     * Searches questions by title with pagination support.
     *
     * @param query the search string (case-insensitive)
     * @param page the page number (0-based)
     * @param size the number of items per page
     * @return a page of matching questions
     */
    fun searchQuestions(query: String, page: Int, size: Int): Page<GetQuestionInfoResponse?>?

    /**
     * Finds questions created by a specific teacher.
     *
     * @param teacherId the UUID of the teacher
     * @param page the page number (0-based)
     * @param size the number of items per page
     * @return a page of questions
     * @throws TeacherNotFoundException if no teacher exists with the given ID
     */
    fun findByTeacher(teacherId: UUID, page: Int, size: Int): Page<GetQuestionInfoResponse?>?

    /**
     * Finds questions associated with any of the specified themes.
     *
     * @param themeIds the list of theme UUIDs to filter by
     * @param page the page number (0-based)
     * @param size the number of items per page
     * @return a page of matching questions
     * @throws ThemeNotFoundException if any theme doesn't exist
     */
    fun findByThemes(themeIds: List<UUID>, page: Int, size: Int): Page<GetQuestionInfoResponse?>?

    /**
     * Validates that a teacher owns the specified question.
     *
     * @param questionId the UUID of the question
     * @param teacherId the UUID of the teacher to validate
     * @throws QuestionNotFoundException if no question exists with the given ID
     * @throws QuestionAccessDeniedException if teacher doesn't own the question
     */
    fun validateQuestionOwnership(questionId: UUID, teacherId: UUID)
}