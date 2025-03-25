package app.core.util

class CommonResponse<T>(val isError: Boolean = false, val error: String? = null, val errorKey: String? = null, val response: T? = null)
