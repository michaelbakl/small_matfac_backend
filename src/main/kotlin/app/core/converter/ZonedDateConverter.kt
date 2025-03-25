package ru.baklykov.app.core.converter

import app.core.converter.IConverter
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object ZonedDateConverter : IConverter<ZonedDateTime?, String?> {

    override fun convert(obj: String?): ZonedDateTime? {
        return ZonedDateTime.parse(obj!!, DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss Z"))
    }
}
