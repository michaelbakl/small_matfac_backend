package app.core.util

class SqlQueryBuilder {
    private val queryBuilder: StringBuilder = StringBuilder()
    private val conditions: MutableList<String> = mutableListOf()
    private val joins: MutableList<String> = mutableListOf()
    private var whereUsed: Boolean = false

    fun select(columns: String): SqlQueryBuilder {
        queryBuilder.append("SELECT $columns ")
        return this
    }

    fun from(table: String): SqlQueryBuilder {
        queryBuilder.append("FROM $table ")
        return this
    }

    fun join(joinType: String? = "LEFT", table: String, condition: String?, param: String? = null): SqlQueryBuilder {
        if (!condition.isNullOrEmpty() && param != null) {
            joins.add("$joinType JOIN $table ON $condition ")
        }
        return this
    }

    fun where(column: String, condition: String?, isAnd: Boolean = true, operand: String = "="): SqlQueryBuilder {
        if (condition != null) {
            addCondition(column, condition, isAnd, operand)
        }
        return this
    }

    fun whereNotNull(column: String, isAnd: Boolean = true): SqlQueryBuilder {
        addCondition(column, null, isAnd, "IS NOT NULL", valueEnclosure = false)
        return this
    }

    fun whereNull(column: String, isAnd: Boolean = true): SqlQueryBuilder {
        addCondition(column, null, isAnd, "IS NULL", valueEnclosure = false)
        return this
    }

    private fun addCondition(
        column: String,
        value: String?,
        isAnd: Boolean,
        operand: String,
        valueEnclosure: Boolean = true
    ) {
        val prefix = if (!whereUsed) {
            whereUsed = true
            "WHERE "
        } else {
            if (isAnd) "AND " else "OR "
        }

        val conditionValue = if (value != null && valueEnclosure) "'$value'" else value ?: ""
        conditions.add("$prefix$column $operand $conditionValue")
    }

    fun and(condition: String?): SqlQueryBuilder {
        if (condition != null) {
            conditions.add("AND $condition")
        }
        return this
    }

    fun or(condition: String?): SqlQueryBuilder {
        if (condition != null) {
            conditions.add("OR $condition")
        }
        return this
    }

    fun orderBy(column: String, ascending: Boolean = true): SqlQueryBuilder {
        queryBuilder.append("ORDER BY $column ${if (ascending) "ASC" else "DESC"} ")
        return this
    }

    fun limit(limit: Int): SqlQueryBuilder {
        queryBuilder.append("LIMIT $limit ")
        return this
    }

    fun offset(offset: Int): SqlQueryBuilder {
        queryBuilder.append("OFFSET $offset ")
        return this
    }

    fun between(
        column: String,
        firstCondition: String? = null,
        secondCondition: String? = null
    ): SqlQueryBuilder {
        if (firstCondition != null && secondCondition != null) {
            if (!whereUsed) {
                conditions.add("WHERE $column BETWEEN '$firstCondition' AND '$secondCondition'")
                whereUsed = true
            } else {
                conditions.add("AND $column BETWEEN '$firstCondition' AND '$secondCondition'")
            }
        }
        return this
    }

    fun `in`(column: String, values: Collection<String>): SqlQueryBuilder {
        if (values.isNotEmpty()) {
            val prefix = if (!whereUsed) {
                whereUsed = true
                "WHERE "
            } else {
                "AND "
            }
            conditions.add("$prefix$column IN (${values.joinToString(", ") { "'$it'" }})")
        }
        return this
    }

    fun apply(block: SqlQueryBuilder.() -> Unit): SqlQueryBuilder {
        this.block()
        return this
    }

    fun build(): String {
        if (joins.isNotEmpty()) {
            queryBuilder.append(joins.joinToString(" "))
        }
        if (conditions.isNotEmpty()) {
            // queryBuilder.append("WHERE ")
            queryBuilder.append(conditions.joinToString(" "))
        }
        return queryBuilder.toString().trim()
    }
}
