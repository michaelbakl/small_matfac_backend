package ru.baklykov.app.core.converter.util

import ru.baklykov.app.core.converter.IListConverter
import ru.baklykov.app.core.converter.IViceVersaConverter
import java.util.*

object IdConverter: IViceVersaConverter<String, UUID>, IListConverter<String, UUID> {

    override fun convertFrom(obj: UUID?): String? {
        return if (obj != null) obj.toString() else null
    }

    override fun convertTo(obj: String?): UUID? {
        return if (obj != null && !"".equals(obj)) UUID.fromString(obj) else null
    }

    override fun convertToFirst(list: List<UUID>?): List<String> {
        val result = mutableListOf<String>()
        list?.let { it.map { item -> result.add(item.toString()) } }
        return result
    }

    override fun convertToSecond(list: List<String>?): List<UUID> {
        val result = mutableListOf<UUID>()
        list?.let { it.map { item -> result.add(UUID.fromString(item)) } }
        return result
    }
}