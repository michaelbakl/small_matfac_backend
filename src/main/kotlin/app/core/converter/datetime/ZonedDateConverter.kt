package ru.baklykov.app.core.converter.datetime

import app.core.converter.IConverter
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object ZonedDateConverter : IConverter<ZonedDateTime?, String?> {

    private val formatterWithMicros = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSXXX").withZone(ZoneId.of("UTC"))
    private val formatterWithMillis = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSXXX").withZone(ZoneId.of("UTC"))

    override fun convert(obj: String?): ZonedDateTime? {
        if (obj.isNullOrBlank()) {
            return null
        }

        try {
            return ZonedDateTime.parse(obj, formatterWithMicros)
        } catch (e: DateTimeParseException) {
            try {
                return ZonedDateTime.parse(obj, formatterWithMillis)
            } catch (e: DateTimeParseException) {
                return null
            }
        }
    }
}
