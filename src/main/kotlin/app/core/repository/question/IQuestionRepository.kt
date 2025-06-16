package ru.baklykov.app.core.repository.question

import org.springframework.data.domain.Pageable
import app.core.model.question.Question
import java.util.*

interface IQuestionRepository {

    /**
     * saves question to storage
     * @param question - question to save
     * @return saved question
     */
    fun addQuestion(question: Question): Int

    /**
     * updates question in the storage
     * @param question with new params
     * @return updated question
     */
    fun updateQuestion(question: Question): Int

    /**
     * removes question from the storage by id
     * @param questionId - id of the question to remove
     * @return 1 if removed, 0 otherwise
     */
    fun deleteQuestionById(questionId: UUID): Int

    /**
     * returns question from the storage by id
     * @param questionId - id pf the question to find
     * @return question
     */
    fun getQuestionById(questionId: UUID): Question?

    /**
     * finds questions that fit the params
     * @param questionId - id of the question
     * @param ownerId - id of the creator of the question
     * @param title - question title
     * @param description - question description
     * @param themes - question themes
     * @param
     * @return list of suitable questions
     */
    fun getQuestionWithParams(
        questionId: UUID? = null,
        ownerId: UUID? = null,
        title: String? = null,
        description: String? = null,
        themes: List<UUID>? = listOf(),
        themesStr: String? = "*",
    ): List<Question>

    /**
     * adds answerToQuestion
     * @param questionId - id of the question
     * @param answer - pair of answer and pair of rightnes and points
     */
    fun addAnswerToQuestion(questionId: UUID, answer: Pair<UUID, Pair<Boolean, Int>>): Int

    /**
     * removes answer from question
     * @param questionId - id of the question
     * @param answerId - id of the answer to remove
     * @return 1 if removed, 0 otherwise
     */
    fun removeAnswerFromQuestion(questionId: UUID, answerId: UUID): Int

    /**
     * checks if question exists in database
     * @param questionId
     * @return true if exists, false otherwise
     */
    fun existsById(questionId: UUID): Boolean

    /**
     * Counts questions containing the specified text in their title.
     *
     * @param query the search string to match against question titles (case-insensitive)
     * @return total count of matching questions
     */
    fun countByTitleContaining(query: String): Long

    /**
     * Counts questions created by a specific teacher.
     *
     * @param teacherId the UUID of the teacher
     * @return total count of questions associated with the teacher
     * @throws IllegalArgumentException if teacherId is null
     */
    fun countByTeacherId(teacherId: UUID): Long

    /**
     * Counts questions associated with any of the specified themes.
     *
     * @param themeIds list of theme UUIDs to filter by
     * @return total count of questions matching any of the themes
     * @throws IllegalArgumentException if themeIds is empty
     */
    fun countByThemeIds(themeIds: List<UUID>): Long

    /**
     * Checks if a question exists and is associated with a specific teacher.
     *
     * @param questionId the UUID of the question to check
     * @param teacherId the UUID of the teacher to verify
     * @return true if the question exists and belongs to the teacher, false otherwise
     * @throws IllegalArgumentException if either ID is null
     */
    fun existsByIdAndTeacherId(questionId: UUID, teacherId: UUID): Boolean

    /**
     * Finds all questions associated with a specific theme.
     *
     * @param themeId the UUID of the theme to filter by
     * @param pageable pagination information (page number, size, and sorting)
     * @return list of questions associated with the theme, ordered by creation date (newest first)
     * @throws IllegalArgumentException if themeId is null
     */
    fun findByThemeId(themeId: UUID, pageable: Pageable): List<Question>

    /**
     * Finds all questions associated with a specific theme (unpaginated version).
     *
     * @param themeId the UUID of the theme to filter by
     * @return list of all questions associated with the theme
     * @throws IllegalArgumentException if themeId is null
     */
    fun findByThemeId(themeId: UUID): List<Question>

    /**
     * Finds questions containing the specified text in their title (case-insensitive)
     *
     * @param query Search string to match against question titles
     * @param pageable Pagination information
     * @return Flow of matching Questions for reactive streaming
     */
    fun findByTitleContaining(query: String, pageable: Pageable): List<Question>

    /**
     * Finds questions containing the specified text in their title (unpaginated)
     *
     * @param query Search string to match against question titles
     * @return List of matching Questions
     */
    fun findByTitleContaining(query: String): List<Question>

    /**
     * Finds question by title
     */
    fun findByTitleAndDescription(title: String, description: String): Question?
}