package app.core.repository.answer

import app.core.model.Answer
import java.util.*

interface IAnswerRepository {

    /**
     * saves answer in the storage
     * @param answer - object to save
     * @return 1 if added, 0 otherwise
     */
    fun addAnswer(answer: Answer): Int

    /**
     * finds object by id
     * @param answerId - id of the answer
     * @return found object
     */
    fun getAnswerById(answerId: UUID): Answer?

    /**
     * updates object in the storage
     * @param answer - object to update
     * @return 1 if updated, 0 otherwise
     */
    fun updateAnswer(answer: Answer): Int

    /**
     * removes answer from the storage
     * @param answerId - id of the object to remove
     * @return 1 if removed, 0 otherwise
     */
    fun removeAnswer(answerId: UUID): Int

    /**
     * adds picture to answer in the storage
     * @param answerId - id pf the answer
     * @param pictureId - id of the picture to save with answer
     * @return 1 if added, 0 otherwise
     */
    fun addPictureToAnswer(answerId: UUID, pictureId: UUID): Int

    /**
     * removes pictures from the storage and saves new ones
     * @param answerId - id of the object
     * @param pictures - pictures to update
     * @return 1 if updated, 0 otherwise
     */
    fun updateAnswerPictures(answerId: UUID, pictures: List<UUID>): Int

    /**
     * unlinks picture and answer
     * @param answerId - id of the answer
     * @param pictureId - id of the picture
     * @return 1 if removed, 0 otherwise
     */
    fun removePictureFromAnswer(answerId: UUID, pictureId: UUID): Int

    /**
     * finds answer by text
     * @param text - text of the answer
     * @return answer object if found, null otherwise
     */
    fun findByText(text: String): Answer?
}
