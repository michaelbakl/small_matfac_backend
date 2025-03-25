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

    fun join(table: String, condition: String?, param: String? = null): SqlQueryBuilder {
        if (condition != null && condition != "" && param != null) {
            joins.add("JOIN $table ON $condition")
        }
        return this
    }

    fun where(column: String, condition: String?, isAnd: Boolean? = true, operand: String? = "="): SqlQueryBuilder {
        if (condition != null) {
            if (!whereUsed) {
                conditions.add("WHERE $column $operand '$condition'")
                whereUsed = true
            } else {
                if (true == isAnd) {
                    conditions.add("AND $column $operand '$condition'")
                } else {
                    conditions.add("OR $column $operand '$condition'")
                }
            }
        }
        return this
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
        queryBuilder.append("ORDER BY $column ")
        if (!ascending) {
            queryBuilder.append("DESC ")
        }
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
