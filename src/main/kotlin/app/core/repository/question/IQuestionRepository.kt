package ru.baklykov.app.core.repository.question

import ru.baklykov.app.core.model.question.Question
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
}