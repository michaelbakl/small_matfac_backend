package app.core.exception

class SignUpException(msg: String, error: Throwable? = null): ServiceException(msg, error)
