package org.phoenix.planet.dto.eco_stock_certificate.request;

import jakarta.validation.constraints.NotBlank;

public record TumblerCertificateRequest(
    @NotBlank String code
) {

}
