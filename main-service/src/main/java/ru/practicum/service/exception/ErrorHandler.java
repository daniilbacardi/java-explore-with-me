package ru.practicum.service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.service.CommonConstants;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler({DatesException.class,
            MethodArgumentTypeMismatchException.class,
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class,
            IllegalArgumentException.class,
            MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiError handleException(final Exception e) {
        log.error(e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .reason("Запрос выполнен некорректно.")
                .message(e.getMessage())
                .errors(List.of(Arrays.toString(e.getStackTrace())))
                .timestamp(LocalDateTime.now().format(CommonConstants.FORMATTER))
                .build();
    }

    @ExceptionHandler({EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiError handleNotFound(final RuntimeException e) {
        log.error(e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.NOT_FOUND.name())
                .reason("Объект не найден.")
                .message(e.getMessage())
                .errors(List.of(Arrays.toString(e.getStackTrace())))
                .timestamp(LocalDateTime.now().format(CommonConstants.FORMATTER))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiError handleInternal(final InternalError e) {
        log.error(e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .reason("Произошла непредвиденная ошибка.")
                .message(e.getMessage())
                .errors(List.of(Arrays.toString(e.getStackTrace())))
                .timestamp(LocalDateTime.now().format(CommonConstants.FORMATTER))
                .build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiError handleDataIntegrityViolation(final DataIntegrityViolationException e) {
        log.error(e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.CONFLICT.name())
                .reason("Нарушена целостность данных.")
                .message(e.getMessage())
                .errors(List.of(Arrays.toString(e.getStackTrace())))
                .timestamp(LocalDateTime.now().format(CommonConstants.FORMATTER))
                .build();
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiError handleConflict(final ConflictException e) {
        log.error(e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.CONFLICT.name())
                .reason("Условия запрошенной операции не были выполнены.")
                .message(e.getMessage())
                .errors(List.of(Arrays.toString(e.getStackTrace())))
                .timestamp(LocalDateTime.now().format(CommonConstants.FORMATTER))
                .build();
    }
}
