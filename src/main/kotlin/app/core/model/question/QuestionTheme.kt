package app.core.model.question

import java.util.*

data class QuestionTheme(
    val id: UUID,
    val path: String, // LTREE в виде строки
    val name: String,
    val level: Int
) {

    fun isChildOf(parentPath: String): Boolean {
        return path.startsWith("$parentPath.")
    }
}
