package ru.baklykov.app.core.converter.datetime

import ru.baklykov.app.core.converter.IViceVersaConverter
import java.sql.Timestamp
import java.time.ZoneId
import java.time.ZonedDateTime

object TimestampzConverter : IViceVersaConverter<Timestamp?, ZonedDateTime?> {
    override fun convertFrom(obj: ZonedDateTime?): Timestamp? {
        return if (obj != null) {
            Timestamp.from(obj.toInstant())
        } else null
    }

    fun convert(obj: Timestamp?, zoneId: ZoneId = ZoneId.of("UTC")): ZonedDateTime? {
        return if (obj != null) {
            ZonedDateTime.ofInstant(obj.toInstant(), zoneId)
        } else null
    }

    override fun convertTo(obj: Timestamp?): ZonedDateTime? {
        return if (obj != null) {
            ZonedDateTime.ofInstant(obj.toInstant(), ZoneId.of("UTC"))
        } else null
    }
}