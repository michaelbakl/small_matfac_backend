package app.core.converter


interface ITripleConverter<T, V, R> {
    fun convertToModel(obj: V): T
    fun convertToResponse(obj: T): R
    fun convertToResponseList(list: List<T>?): List<R>?
}