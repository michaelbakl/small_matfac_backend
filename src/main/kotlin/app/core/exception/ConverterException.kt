package app.core.exception

class ConverterException(error: String, cause: Throwable ?= null): Exception(error, cause)