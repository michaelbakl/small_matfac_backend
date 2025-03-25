package ru.baklykov.app.core.repository.picture

import ru.baklykov.app.core.model.Picture
import java.util.*

interface IPictureRepository {

    /**
     * saves picture in the storage
     * @param picture - picture to save in the storage
     * @return saved picture
     */
    fun addPicture(picture: Picture): Int

    /**
     * updates picture in the storage
     * @param picture - object to update
     * @return updated object
     */
    fun updatePicture(picture: Picture): Int

    /**
     * finds picture by id
     * @param pictureId - id pf the object
     * @return found object or null if not found
     */
    fun getPictureById(pictureId: UUID): Picture?

    /**
     * removes object form the storage
     * @param pictureId - id Of the object
     * @return 1 if removed, 0 otherwise
     */
    fun removePicture(pictureId: UUID): Int
}
