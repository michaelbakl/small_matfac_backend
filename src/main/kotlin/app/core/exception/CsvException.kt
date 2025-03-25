package app.core.exception

class CsvException(error: String, cause: Throwable): ServiceException(error, cause)