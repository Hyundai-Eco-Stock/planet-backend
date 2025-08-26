package org.phoenix.planet.error;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.phoenix.planet.error.auth.TokenException;
import org.phoenix.planet.error.order.OrderException;
import org.phoenix.planet.error.payment.PaymentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    public ResponseEntity<Map<String, Object>> handleValidationException(
        MethodArgumentNotValidException e) {

        Map<String, Object> body = new HashMap<>();
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        String errorMessage = "입력값이 올바르지 않습니다.";

        // 첫 번째 에러 메시지 추출
        if (!allErrors.isEmpty()) {
            errorMessage = allErrors.getFirst().getDefaultMessage();
        }

        body.put("errorCode", e.getClass().getSimpleName());
        body.put("message", errorMessage);

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(body);
    }

    /**
     * JSON 파싱 예외 처리(잘못된 enum)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(
        HttpMessageNotReadableException e) {

        Map<String, Object> body = new HashMap<>();
        String errorMessage = "입력값이 올바르지 않습니다.";

        body.put("errorCode", "INVALID_REQUEST_FORMAT");
        body.put("message", errorMessage);

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(body);
    }


    /**
     * 결제 관련 예외 처리
     */
    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentException(PaymentException e) {

        Map<String, Object> body = new HashMap<>();
        body.put("errorCode", e.getError().name());
        body.put("message", e.getError().getValue());

        return ResponseEntity
                .status(e.getError().getHttpStatus())
                .header("X-Error-Code", e.getError().name())
                .body(body);

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
