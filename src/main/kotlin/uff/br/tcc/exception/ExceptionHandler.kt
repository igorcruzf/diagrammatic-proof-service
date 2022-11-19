package uff.br.tcc.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import java.security.InvalidParameterException

@ControllerAdvice
class ExceptionHandler {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @ExceptionHandler(InvalidParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidParameterException(
        exception: InvalidParameterException
    ): ResponseEntity<ErrorResponse> {
        return buildResponse(
            HttpStatus.BAD_REQUEST,
            exception,
            ErrorResponse(exception.message)
        )
    }

    @ExceptionHandler(RuntimeException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGenericException(
        exception: RuntimeException
    ): ResponseEntity<ErrorResponse> {
        return buildResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            exception,
            ErrorResponse(exception.message)
        )
    }

    private fun buildResponse(
        status: HttpStatus,
        exception: Exception? = null,
        payload: ErrorResponse? = null
    ) = ResponseEntity.status(status).body(payload).also {
        logger.error(
            "Erro: ${exception?.let {
                "exception.class: ${exception.javaClass.name} exception.message ${exception.message}"
            }} payload: $payload"
        )
    }
}
