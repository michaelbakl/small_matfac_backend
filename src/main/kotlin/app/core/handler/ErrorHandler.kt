package ru.baklykov.app.core.handler

import app.core.exception.NotFoundException
import app.core.util.CommonResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.baklykov.app.core.exception.question.QuestionNotFoundException
import ru.baklykov.app.core.exception.question.QuestionValidationException
import ru.baklykov.app.core.exception.theme.ThemeNotFoundException
import ru.baklykov.app.core.model.error.ErrorResponse
import java.time.LocalDateTime

@RestControllerAdvice
class ErrorHandler {

    @ExceptionHandler(NotFoundException::class)
    fun handleObjectNotFound(ex: NotFoundException): ResponseEntity<CommonResponse<ErrorResponse>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                CommonResponse(
                    isError = true,
                    response =
                        ErrorResponse(
                            timestamp = LocalDateTime.now(),
                            status = HttpStatus.NOT_FOUND.value(),
                            error = "Not Found",
                            path = ""
                        )
                )
            )
    }

    @ExceptionHandler(QuestionNotFoundException::class)
    fun handleQuestionNotFound(ex: QuestionNotFoundException): ResponseEntity<CommonResponse<ErrorResponse>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                CommonResponse(
                    isError = true,
                    response =
                        ErrorResponse(
                            timestamp = LocalDateTime.now(),
                            status = HttpStatus.NOT_FOUND.value(),
                            error = "Not Found",
                            path = ""
                        )
                )
            )
    }

    @ExceptionHandler(ThemeNotFoundException::class)
    fun handleThemeNotFound(ex: ThemeNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                ErrorResponse(
                    timestamp = LocalDateTime.now(),
                    status = HttpStatus.NOT_FOUND.value(),
                    error = "Not Found",
                    path = ""
                )
            )
    }

    @ExceptionHandler(QuestionValidationException::class)
    fun handleValidationException(ex: QuestionValidationException): ResponseEntity<CommonResponse<ErrorResponse>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                CommonResponse(
                    isError = true,
                    response = ErrorResponse(
                        timestamp = LocalDateTime.now(),
                        status = HttpStatus.BAD_REQUEST.value(),
                        error = "Bad Request",
                        path = ""
                    )
                )
            )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleIdValidationException(ex: MethodArgumentNotValidException): ResponseEntity<CommonResponse<ErrorResponse>> {
        val errors = ex.bindingResult.allErrors.map { error ->
            "${error.objectName}: ${error.defaultMessage}"
        }
        return ResponseEntity.badRequest().body(
            CommonResponse(
                isError = true,
                response = ErrorResponse(
                    timestamp = LocalDateTime.now(),
                    status = HttpStatus.BAD_REQUEST.value(),
                    error = "Validation failed",
                    path = ""
                )
            )
        )
    }
}
