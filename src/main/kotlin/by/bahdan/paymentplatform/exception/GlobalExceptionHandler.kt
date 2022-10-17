package by.bahdan.paymentplatform.exception

import com.fasterxml.jackson.annotation.JsonProperty
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(value = [PlatformException::class])
    protected fun handlePlatformExceptions(e: PlatformException, request: WebRequest): ResponseEntity<Any> {
        return handleExceptionInternal(
            e,
            ExceptionResponseBody(
                description = e.message,
                errorMessage = e.responseErrorMessage
            ),
            HttpHeaders.EMPTY,
            HttpStatus.valueOf(e.responseCode),
            request
        )
    }

    @ExceptionHandler(value = [Exception::class])
    protected fun handleInternalErrors(e: Exception, request: WebRequest): ResponseEntity<Any> {
        log.error(e.message)
        return handleExceptionInternal(
            e,
            ExceptionResponseBody(
                description = "Unexpected error occurred",
                errorMessage = "unexpected_internal_error"
            ),
            HttpHeaders.EMPTY,
            HttpStatus.INTERNAL_SERVER_ERROR,
            request
        )
    }
}

data class ExceptionResponseBody(
    val description: String?,
    @JsonProperty("error_message") val errorMessage: String
)
