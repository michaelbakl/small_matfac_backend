package app.core.converter

interface IConverter<T, V> {
    fun convert(obj: V): T
}
