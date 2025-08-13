package org.phoenix.planet.error;

import java.util.HashMap;
import java.util.Map;
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

}
