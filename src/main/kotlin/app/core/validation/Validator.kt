package app.core.validation

interface Validator<T> {
    fun validate(obj: T?): Boolean
}