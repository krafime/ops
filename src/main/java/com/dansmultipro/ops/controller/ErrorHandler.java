package com.dansmultipro.ops.controller;

import com.dansmultipro.ops.dto.general.ErrorResDTO;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.DateTimeException;

@RestControllerAdvice
public class ErrorHandler {

    /**
     * Handle business logic validation errors
     * Terjadi ketika: Service layer throw IllegalArgumentException
     * Contoh: Product not found, Insufficient stock, OptLock mismatch
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {
        var errorResponse = new ErrorResDTO<>(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle DTO field validation errors
     * Terjadi ketika: @Valid annotation di controller method parameter
     * Contoh: quantity < 0, price = 0, field required tapi null
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleArgumentNotValid(MethodArgumentNotValidException e) {
        var errors = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        var errorResponse = new ErrorResDTO<>(errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle missing required query parameters
     * Terjadi ketika: User tidak pass required @RequestParam
     * Contoh: GET /api/transactions/date-range tanpa ?startDate=
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
        var errorResponse = new ErrorResDTO<>("Missing required parameter: " + e.getParameterName());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle path/query parameter type mismatch
     * Terjadi ketika: User kirim UUID format salah atau Boolean format salah
     * Contoh: GET /api/inventories/bukan-uuid-format atau ?isActive=maybe
     */
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(TypeMismatchException e) {
        String message = "Invalid parameter format";
        if (e.getValue() != null && e.getRequiredType() != null) {
            message = "Invalid " + e.getPropertyName() + " format: expected " + e.getRequiredType().getSimpleName() + ", got '" + e.getValue() + "'";
        }
        var errorResponse = new ErrorResDTO<>(message);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle Hibernate validation constraint violations
     * Terjadi ketika: Field violate @NotNull, @Positive, etc.
     * Contoh: @Positive quantity tapi user kirim -50
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException e) {
        var errors = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();
        var errorResponse = new ErrorResDTO<>(errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle database constraint violations
     * Terjadi ketika: Unique constraint violated, Foreign key violated, Check constraint violated
     * Contoh: Insert duplicate product code, Delete parent row yang masih di-reference
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        String message = "Data integrity violation";
        if (e.getMessage() != null) {
            if (e.getMessage().contains("unique") || e.getMessage().contains("UNIQUE")) {
                message = "Duplicate entry: Resource already exists";
            } else if (e.getMessage().contains("foreign") || e.getMessage().contains("FOREIGN")) {
                message = "Foreign key constraint violation";
            } else if (e.getMessage().contains("check") || e.getMessage().contains("CHECK")) {
                message = "Check constraint violation";
            }
        }
        var errorResponse = new ErrorResDTO<>(message);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handle JPA object retrieval failure (alternative EntityNotFoundException)
     * Terjadi ketika: JPA proxy gagal retrieve entity
     * Contoh: LAZY loading entity tapi entity sudah deleted
     */
    @ExceptionHandler(JpaObjectRetrievalFailureException.class)
    public ResponseEntity<?> handleJpaObjectRetrievalFailure(JpaObjectRetrievalFailureException e) {
        var errorResponse = new ErrorResDTO<>("Resource not found");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle endpoint not found (404)
     * Terjadi ketika: User akses endpoint yang tidak exist
     * Contoh: GET /api/invalid-endpoint
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceFound(NoResourceFoundException e) {
        var errorResponse = new ErrorResDTO<>("Endpoint not found: " + e.getResourcePath());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle invalid JSON format
     * Terjadi ketika: Request body JSON malformed atau incomplete
     * Contoh: { "name": "test } (missing closing brace) atau invalid type conversion
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        var errorResponse = new ErrorResDTO<>("Invalid JSON Format");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle wrong HTTP method
     * Terjadi ketika: User gunakan HTTP method yang salah
     * Contoh: POST ke endpoint GET atau DELETE ke endpoint PUT
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        var errorResponse = new ErrorResDTO<>("HTTP Method not supported");
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Handle unsupported media type
     * Terjadi ketika: User kirim request dengan Content-Type yang tidak di-support
     * Contoh: Content-Type: application/xml (tapi API hanya support application/json)
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        var errorResponse = new ErrorResDTO<>("Media type not supported");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * Handle null pointer exception
     * Terjadi ketika: Ada null reference di code (server error)
     * Contoh: entity.getProperty() dimana entity null, atau lazy loading gagal
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointer(NullPointerException e) {
        var errorResponse = new ErrorResDTO<>("Internal server error: null reference");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DateTimeException.class)
    public ResponseEntity<?> handleDateTimeException(DateTimeException e) {
        var errorResponse = new ErrorResDTO<>("Format or date not valid");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}

