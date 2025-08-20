package org.phoenix.planet.error;

import java.util.HashMap;
import java.util.Map;
import org.phoenix.planet.error.auth.TokenException;
import org.phoenix.planet.error.order.OrderException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
        IllegalArgumentException e) {

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

}
