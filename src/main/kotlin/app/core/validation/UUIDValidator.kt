package ru.baklykov.app.core.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import java.util.*

class UUIDValidator : ConstraintValidator<ValidUUID, Any> {
    override fun isValid(value: Any?, context: ConstraintValidatorContext): Boolean {
        return when (value) {
            null -> true
            is UUID -> true // UUID уже валиден по определению
            is String -> try {
                UUID.fromString(value)
                true
            } catch (e: IllegalArgumentException) {
                false
            }
            else -> false
        }
    }
}