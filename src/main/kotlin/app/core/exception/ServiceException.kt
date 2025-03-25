package app.core.exception

open class ServiceException(msg: String, error: Throwable? = null): Exception(msg, error)