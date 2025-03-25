package app.core.exception

class NotFoundException(error: String, cause: Throwable? = null): ServiceException(error, cause)