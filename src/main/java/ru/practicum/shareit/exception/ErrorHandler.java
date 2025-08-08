package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Код ответа 400
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.warn("Error", e);
        return new ErrorResponse(e.getMessage());
    }

    // Обработчик NotFoundException
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // Код ответа 404
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.warn("Error", e);
        return new ErrorResponse(e.getMessage());
    }

    // Обработчик DuplicateExecution
    @ExceptionHandler(DuplicateExecution.class)
    @ResponseStatus(HttpStatus.CONFLICT) // Код ответа 409
    public ErrorResponse handleDuplicateExecution(final DuplicateExecution e) {
        log.warn("Error", e);
        return new ErrorResponse(e.getMessage());
    }

    // Обработчик любых других исключений
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // Код ответа 500
    public ErrorResponse handleThrowable(final Throwable e) {
        log.warn("Error", e);
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }
}