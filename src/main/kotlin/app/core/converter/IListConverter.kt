package ru.baklykov.app.core.converter

interface IListConverter<T, V> {

    fun convertToFirst(list: List<V>?): List<T>

    fun convertToSecond(list: List<T>?): List<V>
}
