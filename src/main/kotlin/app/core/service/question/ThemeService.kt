package ru.baklykov.app.core.service.question

import app.core.exception.NotFoundException
import app.core.exception.RepositoryException
import app.core.exception.ServiceException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.baklykov.app.core.model.question.QuestionTheme
import ru.baklykov.app.core.repository.question.IQuestionRepository
import ru.baklykov.app.core.repository.question.IQuestionThemeRepository
import ru.baklykov.app.web.model.response.theme.ThemeResponse
import java.util.*

@Service
class ThemeService(private val themeRepository: IQuestionThemeRepository, private val questionRepository: IQuestionRepository): IThemeService {

    private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    @CacheEvict(value = ["themeHierarchy"], allEntries = true)
    override suspend fun createTheme(name: String, parentThemeId: UUID?): QuestionTheme {
        try {
            LOGGER.debug("SERVICE create theme {} {}", name, parentThemeId)
            val parentTheme = parentThemeId?.let { themeRepository.findById(it) }

            val level = (parentTheme?.level ?: 0) + 1
            val path = parentTheme?.let { "${it.path}.${generatePathPart(name)}" } ?: generatePathPart(name)

            val newTheme = QuestionTheme(
                id = UUID.randomUUID(),
                path = path,
                name = name,
                level = level
            )

            themeRepository.saveTheme(newTheme)
            return getTheme(newTheme.id)
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error creating theme {} {}", name, parentThemeId, e)
            throw ServiceException("SERVICE create theme exception", e)
        }
    }

    @Transactional
    @CacheEvict(value = ["theme", "themeHierarchy"], key = "#themeId")
    override suspend fun updateTheme(themeId: UUID, newName: String): QuestionTheme {
        try {
            LOGGER.debug("SERVICE update theme {} {}", themeId, newName)

            val existingTheme = themeRepository.findById(themeId) ?: throw NotFoundException("Theme was not found by id $themeId")

            val updatedTheme = existingTheme.copy(name = newName)
            themeRepository.saveTheme(updatedTheme)

            return getTheme(themeId)
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error updating theme {} {}", themeId, newName, e)
            throw ServiceException("SERVICE update theme exception", e)
        }
    }

    @Cacheable("theme", key = "#themeId")
    override suspend fun getTheme(themeId: UUID): QuestionTheme {
        try {
            LOGGER.debug("SERVICE get theme {}", themeId)
            return themeRepository.findById(themeId) ?: throw NotFoundException("Theme was not found by id $themeId")
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error getting theme {}", themeId, e)
            throw ServiceException("SERVICE get theme exception", e)
        }
    }

    @Transactional
    @CacheEvict(value = ["theme", "themeHierarchy"], allEntries = true)
    override suspend fun deleteTheme(themeId: UUID): Boolean {
        try {
            LOGGER.debug("SERVICE delete theme {}", themeId)
            return themeRepository.deleteTheme(themeId)
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error deleting theme {}", themeId, e)
            throw ServiceException("SERVICE delete theme exception", e)
        }
    }

    @Cacheable("themeHierarchy", key = "#rootThemeId ?: 'root'")
    override suspend fun getThemeHierarchy(rootThemeId: UUID?): List<QuestionTheme> {
        try {
            LOGGER.debug("SERVICE get theme hierarchy by root id {}", rootThemeId)
            val rootThemes = if (rootThemeId != null ) themeRepository.findChildThemes(rootThemeId) else listOf()
            return  rootThemes
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error getting theme hierarchy by root id {}", rootThemeId, e)
            throw ServiceException("SERVICE get theme hierarchy by root id exception", e)
        }
    }

    override suspend fun getParentThemes(themeId: UUID): List<QuestionTheme> {
        try {
            LOGGER.debug("SERVICE get parent themes by theme id {}", themeId)
            return themeRepository.getParentThemes(themeId)
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error getting parent themes by theme id {}", themeId, e)
            throw ServiceException("SERVICE get parent themes by theme id exception", e)
        }
    }

    override suspend fun getChildThemes(themeId: UUID): List<QuestionTheme> {
        try {
            LOGGER.debug("SERVICE get child themes by theme id {}", themeId)
            return themeRepository.findChildThemes(themeId)
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error getting child themes by theme id {}", themeId, e)
            throw ServiceException("SERVICE get child themes by theme id exception", e)
        }
    }

    @Transactional
    override suspend fun addThemeToQuestion(questionId: UUID, themeId: UUID) {
        try {
            LOGGER.debug("SERVICE add theme to question by id {}, {}", questionId, themeId)
            questionRepository.existsById(questionId) || throw NotFoundException("Question was not found by $questionId")
            themeRepository.findById(themeId) ?: throw NotFoundException("Theme was not found by $themeId")

            themeRepository.addThemeToQuestion(questionId, themeId)
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error adding theme to question by id {}", themeId, e)
            throw ServiceException("SERVICE add theme to question by id exception", e)
        }
    }

    @Transactional
    override suspend fun removeThemeFromQuestion(questionId: UUID, themeId: UUID) {
        try {
            LOGGER.debug("SERVICE remove theme from question by id {}, {}", questionId, themeId)
            themeRepository.removeThemeFromQuestion(questionId, themeId)
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error removing theme from question by id {}", themeId, e)
            throw ServiceException("SERVICE remove theme from question by id exception", e)
        }
    }

    override suspend fun getQuestionThemes(questionId: UUID): List<QuestionTheme> {
        try {
            LOGGER.debug("SERVICE get question themes by id {}", questionId)
            return themeRepository.findByQuestionId(questionId)
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error getting question themes by id {}", questionId, e)
            throw ServiceException("SERVICE get question themes by id exception", e)
        }
    }

    @Transactional
    override suspend fun setQuestionThemes(questionId: UUID, themeIds: List<UUID>) {
        try {
            LOGGER.debug("SERVICE set question themes by id {} {}", questionId, themeIds)

            questionRepository.existsById(questionId) || throw NotFoundException("Question was not found by $questionId")

            themeIds.forEach { themeId ->
                themeRepository.findById(themeId) ?: throw NotFoundException("Theme was not found by $themeId")
            }

            themeIds.forEach { themeId ->
                themeRepository.removeThemeFromQuestion(questionId, themeId)
            }

            themeIds.forEach { themeId ->
                themeRepository.addThemeToQuestion(questionId, themeId)
            }

        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error setting question themes by id {} {}", questionId, themeIds, e)
            throw ServiceException("SERVICE set question themes by id exception", e)
        }
    }

    override suspend fun searchThemes(query: String, limit: Int): List<QuestionTheme> {
        try {
            LOGGER.debug("SERVICE search themes by query {} {}", query, limit)
            return themeRepository.searchThemes(query, limit)
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error searching themes by query {} {}", query, limit, e)
            throw ServiceException("SERVICE search themes by query exception", e)
        }
    }

    override suspend fun validateThemePath(themeId: UUID, newParentId: UUID?): Boolean {
        try {
            LOGGER.debug("SERVICE validate theme path {} {}", themeId, newParentId)

            if (newParentId == null) return true // Перемещение в корень

            val theme = themeRepository.findById(themeId) ?: return false
            val newParent = themeRepository.findById(newParentId) ?: return false

            // Нельзя сделать родителем самого себя
            if (theme.id == newParent.id) return false

            // Нельзя сделать родителем своего потомка
            return !themeRepository.isDescendant(parentId = themeId, childId = newParentId)

        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE error validating theme path {} {}", themeId, newParentId, e)
            throw ServiceException("SERVICE validate theme path exception", e)
        }
    }

    override suspend fun convertToResponse(theme: QuestionTheme): ThemeResponse {
        return ThemeResponse(
            id = theme.id,
            name = theme.name,
            path = theme.path,
            level = theme.level,
            parentId = if (theme.level > 1) {
                themeRepository.findIdByPath(theme.path.substringBeforeLast('.'))
            } else null,
            hasChildren = themeRepository.findChildThemes(theme.id).isEmpty()
        )
    }

    private fun generatePathPart(name: String): String {
        return name.lowercase()
            .replace("[^a-z0-9а-яё]".toRegex(), "_")
            .replace("_+".toRegex(), "_")
            .trim('_')
    }

}
