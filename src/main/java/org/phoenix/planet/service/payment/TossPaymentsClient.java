package org.phoenix.planet.service.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.payment.response.TossPaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPaymentsClient {

    private final RestTemplate restTemplate;

    @Value("${toss.payments.secret-key}")
    private String tossSecretKey;

    @Value("${toss.payments.api-url:https://api.tosspayments.com}")
    private String baseUrl;

    /**
     * 결제 승인 API 호출
     */
    public TossPaymentResponse confirmPayment(String paymentKey, String orderId, Integer amount) {
        String url = baseUrl + "/v1/payments/confirm";

        // 요청 헤더 설정 (Basic Auth)
        HttpHeaders headers = createHeaders();

        // 요청 바디 설정
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("paymentKey", paymentKey);
        requestBody.put("orderId", orderId);
        requestBody.put("amount", amount);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            log.info("TossPayments 결제 승인 요청: paymentKey={}, orderId={}, amount={}",
                    paymentKey, orderId, amount);

            ResponseEntity<TossPaymentResponse> response = restTemplate.postForEntity(url, request, TossPaymentResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                TossPaymentResponse result = response.getBody();
                log.info("TossPayments 결제 승인 성공: paymentKey={}, status={}", paymentKey, result.status());
                return result;
            } else {
                log.error("TossPayments 결제 승인 응답 오류: statusCode={}", response.getStatusCode());
                throw new TossPaymentsException("결제 승인 응답이 올바르지 않습니다.");
            }
        } catch (HttpClientErrorException e) {
            log.error("TossPayments 클라이언트 오류: statusCode={}, responseBody={}", e.getStatusCode(), e.getResponseBodyAsString());
            String errorMessage = parseErrorMessage(e.getResponseBodyAsString());
            throw new TossPaymentsException("결제 승인 중 클라이언트 오류가 발생했습니다: " + errorMessage);
        } catch (HttpServerErrorException e) {
            log.error("TossPayments 서버 오류: statusCode={}, responseBody={}",  e.getStatusCode(), e.getResponseBodyAsString());
            throw new TossPaymentsException("결제 승인 중 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        } catch (Exception e) {
            log.error("TossPayments API 호출 중 예상치 못한 오류 발생", e);
            throw new TossPaymentsException("결제 승인 중 시스템 오류가 발생했습니다.");
        }
    }

    /**
     * 결제 정보 조회 API 호출
     */
    public TossPaymentResponse getPayment(String paymentKey) {
        String url = baseUrl + "/v1/payments/" + paymentKey;

        HttpHeaders headers = createHeaders();
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            log.info("TossPayments 결제 정보 조회: paymentKey={}", paymentKey);

            ResponseEntity<TossPaymentResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, request, TossPaymentResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new TossPaymentsException("결제 정보 조회 응답이 올바르지 않습니다.");
            }
        } catch (HttpClientErrorException e) {
            log.error("TossPayments 클라이언트 오류: statusCode={}, responseBody={}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new TossPaymentsException("결제 정보 조회 중 오류가 발생했습니다: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("TossPayments API 호출 중 예상치 못한 오류 발생", e);
            throw new TossPaymentsException("결제 정보 조회 중 시스템 오류가 발생했습니다.");
        }
    }

    /**
     * HTTP 헤더 생성 (Basic Auth 인증)
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + encodeSecretKey());
        return headers;
    }

    /**
     * TossPayments 시크릿 키를 Base64로 인코딩 (Basic Auth)
     */
    private String encodeSecretKey() {
        return Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 에러 응답에서 메시지 추출
     */
    private String parseErrorMessage(String responseBody) {
        try {
            // 간단한 에러 메시지 추출
            if (responseBody != null && responseBody.contains("message")) {
                return "TossPayments 오류 응답";
            }
            return "알 수 없는 오류";
        } catch (Exception e) {
            return "에러 메시지 파싱 실패";
        }
    }

    public TossPaymentResponse cancelPayment(String paymentKey, Integer cancelAmount, String cancelReason) {
        String url = baseUrl + "/v1/payments/" + paymentKey + "/cancel";

        // 요청 헤더 설정
        HttpHeaders headers = createHeaders();

        // 요청 바디 설정
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("cancelAmount", cancelAmount);
        requestBody.put("cancelReason", cancelReason);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            log.info("TossPayments 전체 취소 요청 - paymentKey: {}, amount: {}, reason: {}", paymentKey, cancelAmount, cancelReason);

            ResponseEntity<TossPaymentResponse> response = restTemplate.postForEntity(url, request, TossPaymentResponse.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                TossPaymentResponse result = response.getBody();
                log.info("TossPayments 취소 완료 - paymentKey: {}, status: {}", paymentKey, result.status());
                return result;
            } else {
                log.error("TossPayments 취소 응답 오류: statusCode={}", response.getStatusCode());
                throw new TossPaymentsException("결제 취소 응답이 올바르지 않습니다.");
            }
        } catch (HttpClientErrorException e) {
            log.error("TossPayments 클라이언트 오류: statusCode={}, responseBody={}", e.getStatusCode(), e.getResponseBodyAsString());
            String errorMessage = parseErrorMessage(e.getResponseBodyAsString());
            throw new TossPaymentsException("결제 취소 중 클라이언트 오류가 발생했습니다: " + errorMessage);
        } catch (HttpServerErrorException e) {
            log.error("TossPayments 서버 오류: statusCode={}, responseBody={}",  e.getStatusCode(), e.getResponseBodyAsString());
            throw new TossPaymentsException("결제 취소 중 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        } catch (Exception e) {
            log.error("TossPayments API 호출 중 예상치 못한 오류 발생", e);
            throw new TossPaymentsException("결제 취소 중 시스템 오류가 발생했습니다.");
        }
    }

    /**
     * TossPayments API 관련 예외 클래스
     */
    public static class TossPaymentsException extends RuntimeException {
        public TossPaymentsException(String message) {
            super(message);
        }

        public TossPaymentsException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}