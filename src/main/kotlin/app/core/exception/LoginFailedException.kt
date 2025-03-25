package app.core.exception

class LoginFailedException(msg: String, error: Throwable? = null): ServiceException(msg, error)
