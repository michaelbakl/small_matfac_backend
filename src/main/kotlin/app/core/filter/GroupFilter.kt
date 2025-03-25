package app.core.filter

import java.sql.Timestamp
import java.util.*

data class GroupFilter (

    val groupId: UUID?,
    val name: String?,
    val firstDate: Timestamp?,
    val secondDate: Timestamp?,
    val classNum: Int?

)
