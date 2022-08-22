package edu.kit.fallob.springConfig;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Objects;

@ControllerAdvice
public class RestExceptionHandling extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        StringBuilder message = new StringBuilder();
        message.append(ex.getLocalizedMessage()).append("\n");
        message.append("errors :");
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            message.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("\n");
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            message.append(error.getObjectName()).append(": ").append(error.getDefaultMessage()).append("\n");
        }

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, message.toString());
        return handleExceptionInternal(ex, apiError, headers, apiError.getStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";
        String message = ex.getLocalizedMessage();

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, message + "\n" + error);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }
    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        StringBuilder message = new StringBuilder();
        message.append(ex.getLocalizedMessage()).append("\n");
        message.append("errors : ");
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            message.append(violation.getRootBeanClass().getName()).append(" ").append(violation.getPropertyPath())
                    .append(": ").append(violation.getMessage()).append("\n");
        }

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, message.toString());
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }
    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        String error = ex.getName() + " should be of type " + Objects.requireNonNull(ex.getRequiredType()).getName();
        String message = ex.getLocalizedMessage();

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, message + "\n" + error);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
        String message = ex.getLocalizedMessage();

        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, message + "\n" + error);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        StringBuilder message = new StringBuilder(ex.getLocalizedMessage() + "\n");
        message.append(ex.getMethod());
        message.append(" method is not supported for this request. Supported methods are ");
        Objects.requireNonNull(ex.getSupportedHttpMethods()).forEach(t -> message.append(t).append(" "));

        ApiError apiError = new ApiError(HttpStatus.METHOD_NOT_ALLOWED, message.toString());
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        StringBuilder message = new StringBuilder(ex.getLocalizedMessage() + "\n");
        message.append(ex.getContentType());
        message.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> message.append(t).append(", "));

        ApiError apiError = new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, message.substring(0, message.length() - 2));
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        StringBuilder message = new StringBuilder(ex.getLocalizedMessage() + "\n");

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, message.substring(0, message.length() - 2));
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage());
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
