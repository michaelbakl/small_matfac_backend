package ru.baklykov.app.core.service.question

import app.core.exception.ServiceException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.transaction.annotation.Transactional
import ru.baklykov.app.core.exception.question.QuestionNotFoundException
import ru.baklykov.app.core.extension.toResponse
import ru.baklykov.app.core.model.question.Question
import ru.baklykov.app.core.repository.question.IQuestionRepository
import ru.baklykov.app.core.repository.question.IQuestionThemeRepository
import ru.baklykov.app.web.model.request.question.CreateQuestionRequest
import ru.baklykov.app.web.model.request.question.UpdateQuestionRequest
import ru.baklykov.app.web.model.response.question.GetQuestionInfoResponse
import ru.baklykov.app.web.model.response.theme.ThemeResponse
import java.util.*

class QuestionService(
    private val questionRepository: IQuestionRepository,
    private val themeRepository: IQuestionThemeRepository,
    private val themeService: IThemeService
): IQuestionService {

    private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override suspend fun createQuestion(ownerId: String, request: CreateQuestionRequest): GetQuestionInfoResponse? {
        val question = Question(
            questionId = UUID.randomUUID(),
            title = request.title,
            description = request.description,
            pictures = request.pictures.map { UUID.fromString(it) },
            answers = request.answers,
            ownerId = UUID.fromString(ownerId),
            type = request.type
        )

        questionRepository.addQuestion(question)
        val savedQuestion = questionRepository.getQuestionById(question.questionId)

        request.themeIds?.let { themeService.setQuestionThemes(savedQuestion.questionId, it) }

        return savedQuestion.toResponse(themeRepository)
    }

    @Transactional(readOnly = true)
    override fun getQuestion(questionId: UUID): GetQuestionInfoResponse? {
        val question = questionRepository.getQuestionById(questionId)
            ?: throw QuestionNotFoundException(questionId)

        return question.toResponse(themeRepository)
    }

    override fun updateQuestion(questionId: UUID, request: UpdateQuestionRequest): GetQuestionInfoResponse? {
        TODO("Not yet implemented")
    }

    override fun deleteQuestion(questionId: UUID) {
        TODO("Not yet implemented")
    }

    override fun addThemeToQuestion(questionId: UUID, themeId: UUID) {
        TODO("Not yet implemented")
    }

    override fun removeThemeFromQuestion(questionId: UUID, themeId: UUID) {
        TODO("Not yet implemented")
    }

    override fun setQuestionThemes(questionId: UUID, themeIds: List<UUID>) {
        TODO("Not yet implemented")
    }

    override fun getQuestionThemes(questionId: UUID): List<ThemeResponse?>? {
        TODO("Not yet implemented")
    }

    override fun searchQuestions(query: String, page: Int, size: Int): Page<GetQuestionInfoResponse?>? {
        TODO("Not yet implemented")
    }

    override fun findByTeacher(teacherId: UUID, page: Int, size: Int): Page<GetQuestionInfoResponse?>? {
        TODO("Not yet implemented")
    }

    override fun findByThemes(themeIds: List<UUID?>, page: Int, size: Int): Page<GetQuestionInfoResponse?>? {
        TODO("Not yet implemented")
    }

    override fun validateQuestionOwnership(questionId: UUID?, teacherId: UUID?) {
        TODO("Not yet implemented")
    }
}