package app.core.service.question

import app.core.repository.answer.IAnswerRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.baklykov.app.core.exception.question.QuestionAccessDeniedException
import ru.baklykov.app.core.exception.question.QuestionCreationException
import ru.baklykov.app.core.exception.question.QuestionNotFoundException
import app.core.extension.toResponse
import app.core.model.question.Question
import ru.baklykov.app.core.repository.question.IQuestionRepository
import ru.baklykov.app.core.repository.question.IQuestionThemeRepository
import app.web.model.request.question.CreateQuestionRequest
import app.web.model.request.question.UpdateQuestionRequest
import app.core.model.Answer
import ru.baklykov.app.core.service.question.IQuestionService
import ru.baklykov.app.core.service.question.IThemeService
import app.web.model.response.question.GetQuestionInfoResponse
import java.util.*

@Service
open class QuestionService(
    private val questionRepository: IQuestionRepository,
    private val themeRepository: IQuestionThemeRepository,
    private val answerRepository: IAnswerRepository,
    private val themeService: IThemeService
) : IQuestionService {

    private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun createQuestion(ownerId: String, request: CreateQuestionRequest): GetQuestionInfoResponse {
        val existingQuestion = questionRepository.findByTitleAndDescription(request.title, request.description)
        val questionId = existingQuestion?.questionId ?: UUID.randomUUID()

        if (existingQuestion == null) {
            val newQuestion = Question(
                questionId = questionId,
                title = request.title,
                description = request.description,
                pictures = request.pictures.map { UUID.fromString(it) },
                answers = emptyList(),
                ownerId = UUID.fromString(ownerId),
                type = request.type
            )
            questionRepository.addQuestion(newQuestion)
        }

        for (answer in request.answers) {
            val existingAnswer = answerRepository.findByText(answer.text)
            val answerId = existingAnswer?.answerId ?: UUID.randomUUID()

            if (existingAnswer == null) {
                answerRepository.addAnswer(Answer(answerId, answer.text))
            }

            questionRepository.addAnswerToQuestion(
                questionId,
                Pair(answerId, Pair(answer.correct, answer.points))
            )
        }

        val savedQuestion = questionRepository.getQuestionById(questionId)

        if (savedQuestion != null) {
            request.themeIds?.let {
                themeService.setQuestionThemes(savedQuestion.questionId, it)
            }
            return savedQuestion.toResponse(themeRepository)
        }

        throw QuestionCreationException("Question could not be created")
    }


    @Transactional(readOnly = true)
    override fun getQuestion(questionId: UUID): GetQuestionInfoResponse {
        val question = questionRepository.getQuestionById(questionId)
            ?: throw QuestionNotFoundException(questionId)

        return question.toResponse(themeRepository)
    }

    @Transactional
    override fun updateQuestion(questionId: UUID, request: UpdateQuestionRequest): GetQuestionInfoResponse {
        val existingQuestion = questionRepository.getQuestionById(questionId)
            ?: throw QuestionNotFoundException(questionId)

        val updatedQuestion = existingQuestion.copy(
            title = request.title,
            description = request.description,
            pictures = request.pictures.map { UUID.fromString(it) },
            answers = request.answers
        )

        questionRepository.updateQuestion(updatedQuestion)

        request.themeIds?.let {
            themeService.setQuestionThemes(questionId, it)
        }

        return updatedQuestion.toResponse(themeRepository)
    }

    @Transactional
    override fun deleteQuestion(questionId: UUID) {
        questionRepository.deleteQuestionById(questionId)
    }

    @Transactional(readOnly = true)
    override fun searchQuestions(query: String, pageable: Pageable): Page<GetQuestionInfoResponse?> {
        val questions = questionRepository.findByTitleContaining(query, pageable)
        val total = questionRepository.countByTitleContaining(query)

        return PageImpl(
            questions.map { it.toResponse(themeRepository) },
            pageable,
            total
        )
    }

    @Transactional(readOnly = true)
    override fun findByTeacher(teacherId: UUID, page: Int, size: Int): Page<GetQuestionInfoResponse?>? {
        val pageable = PageRequest.of(page, size)
        val questions = questionRepository.getQuestionWithParams(teacherId)
            .drop(pageable.pageNumber * pageable.pageSize)
            .take(pageable.pageSize)
            .map { it.toResponse(themeRepository) }

        return PageImpl(questions, pageable, questionRepository.countByTeacherId(teacherId))
    }

    @Transactional(readOnly = true)
    override fun findByThemes(themeIds: List<UUID>, page: Int, size: Int): Page<GetQuestionInfoResponse?> {
        val pageable = PageRequest.of(page, size)
        val questions = themeIds.flatMap { themeId ->
            questionRepository.findByThemeId(themeId)
        }.distinct()
            .drop(pageable.pageNumber * pageable.pageSize)
            .take(pageable.pageSize)
            .map { it.toResponse(themeRepository) }

        return PageImpl(questions, pageable, questionRepository.countByThemeIds(themeIds))
    }

    @Transactional(readOnly = true)
    override fun validateQuestionOwnership(questionId: UUID, teacherId: UUID) {
        if (!questionRepository.existsByIdAndTeacherId(questionId, teacherId)) {
            throw QuestionAccessDeniedException(questionId)
        }
    }
}