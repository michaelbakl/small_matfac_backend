package ru.baklykov.app.core.converter

interface IViceVersaConverter<T, V> {

    fun convertFrom(obj: V?): T?

    fun convertTo(obj: T?): V?
}