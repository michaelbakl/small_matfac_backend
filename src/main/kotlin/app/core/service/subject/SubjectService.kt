package app.core.service.subject

import app.core.exception.NotFoundException
import app.core.exception.RepositoryException
import app.core.exception.ServiceException
import app.core.filter.SubjectFilter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import app.core.repository.subject.ISubjectRepository
import app.web.model.request.subject.GetSubjectsWithParamsRequest
import app.web.model.response.subject.GetSubjectsResponse
import app.web.model.response.subject.GetSubjectResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import app.core.model.Subject
import java.util.UUID

@Service
open class SubjectService(private val repository: ISubjectRepository) : ISubjectService {

    private val LOGGER: Logger = LoggerFactory.getLogger(SubjectService::class.java)

    @Transactional
    override fun addSubject(request: Subject): GetSubjectResponse {
        LOGGER.debug("SERVICE add subject by request {}", request)
        try {
            repository.addSubject(request)
            return getSubjectById(request.id.toString())
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE can't add subject by request {}", request)
            throw ServiceException("SERVICE adding subject by request exception", e)
        }

    }

    @Transactional
    override fun updateSubject(request: Subject): GetSubjectResponse {
        LOGGER.debug("SERVICE update subject by request")
        try {
            repository.updateSubject(request)
            return getSubjectById(request.id.toString())
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE can`t update subject {}", request)
            throw ServiceException("SERVICE updating subject exception", e)
        }
    }

    @Transactional
    override fun deleteSubjectById(id: String): GetSubjectResponse {
        LOGGER.debug("SERVICE delete subject by id {}", id)
        try {
            if (repository.delete(UUID.fromString(id)) == 1) {
                return GetSubjectResponse("Deleted", "Deleted")
            }
            throw NotFoundException("", null)
        } catch (e: RepositoryException) {
            LOGGER.debug("SERVICE can`t delete subject by id {}", id)
            throw ServiceException("SERVICE deleting subject exception", e)
        }
    }

    override fun getSubjectById(id: String): GetSubjectResponse {
        LOGGER.debug("SERVICE get subject by id {}", id)
        try {
            val subject: Subject = repository.getById(UUID.fromString(id)) ?: throw NotFoundException("Subject not found")
            return GetSubjectResponse(subject.id.toString(), subject.name)
        } catch (e: RepositoryException) {
            LOGGER.error("SERVICE can't get subject by id {}", id)
            throw ServiceException("SERVICE can't get subject by id exception", e)
        } catch (e: Exception) {
            LOGGER.error("SERVICE can't get subject by id {}", id)
            throw NotFoundException("Subject not found", e)
        }
    }

    override fun getSubjectsByFilter(request: GetSubjectsWithParamsRequest): GetSubjectsResponse {
        LOGGER.debug("SERVICE get subjects by filter {}", request)
        try {
            val list: List<Subject> = repository.getByFilter(SubjectFilter(request.id, request.name))
            return GetSubjectsResponse(convertObjToRespList(list).toTypedArray())
        } catch (e: Exception) {
            LOGGER.error("SERVICE can't get subjects by filter {}", request)
            throw ServiceException("SERVICE can't get subjects by filter exception", e)
        } catch (e: NullPointerException) {
            LOGGER.error("SERVICE can't get subjects by filter {}", request)
            throw NotFoundException("Subjects not found", e)
        }
    }

    override fun getAll(): GetSubjectsResponse {
        LOGGER.debug("SERVICE get all subjects")
        try {
            val list: List<Subject> = repository.getAll()
            return GetSubjectsResponse(convertObjToRespList(list).toTypedArray())
        } catch (e: NullPointerException) {
            LOGGER.error("SERVICE can't get subjects by filter")
            throw NotFoundException("Subjects not found", e)
        } catch (e: Exception) {
            LOGGER.error("SERVICE can't get subjects by filter")
            throw ServiceException("SERVICE can't get subjects by filter exception", e)
        }
    }

    private fun convertObjToRespList(list: List<Subject>): List<GetSubjectResponse> {
        val resultList: MutableList<GetSubjectResponse> = ArrayList()
        list.map { item -> resultList.add(GetSubjectResponse(item.id.toString(), item.name))}
        return resultList
    }


}