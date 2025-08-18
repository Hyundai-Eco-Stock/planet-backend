package org.phoenix.planet.error;

import java.util.HashMap;
import java.util.Map;

import org.phoenix.planet.error.auth.TokenException;
import org.phoenix.planet.error.order.OrderException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<Map<String, Object>> handleTokenException(TokenException e) {

        Map<String, Object> body = new HashMap<>();
        body.put("errorCode", e.getError().name());
        body.put("message", e.getError().getValue());

        return ResponseEntity
                .status(e.getError().getHttpStatus())
                .header("X-Error-Code", e.getError().name())
                .body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(Exception e) {

        Map<String, Object> body = new HashMap<>();
        body.put("errorCode", e.getClass().getName());
        body.put("message", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {

        Map<String, Object> body = new HashMap<>();
        body.put("errorCode", e.getClass().getName());
        body.put("message", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    /**
     * 주문 관련 예외 처리
     */
    @ExceptionHandler(OrderException.class)
    public ResponseEntity<Map<String, Object>> handleOrderException(OrderException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("errorCode", e.getError().name());
        body.put("message", e.getError().getValue());

        return ResponseEntity
                .status(e.getError().getHttpStatus())
                .header("X-Error-Code", e.getError().name())
                .body(body);
    }

    /**
     * 유효성 검사 실패 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, Object> body = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        if (e.getBindingResult() != null) {
            e.getBindingResult().getAllErrors().forEach((error) -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
        }

        body.put("errorCode", e.getClass().getSimpleName());
        body.put("message", e.getMessage());
        body.put("errors", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

}
