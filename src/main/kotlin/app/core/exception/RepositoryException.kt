package app.core.exception

class RepositoryException(var error: String, cause: Throwable) : Exception(error, cause)