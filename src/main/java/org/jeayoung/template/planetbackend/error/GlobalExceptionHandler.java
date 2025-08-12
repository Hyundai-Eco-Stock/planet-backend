package org.jeayoung.template.planetbackend.error;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
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
}
