package ru.baklykov.app.core.converter.datetime

import app.core.converter.IConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LocalDateConverter : IConverter<LocalDateTime?, String?> {

    override fun convert(obj: String?): LocalDateTime? {
        return LocalDateTime.parse(obj!!, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"))
    }
}
