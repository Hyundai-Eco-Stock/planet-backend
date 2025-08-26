package org.phoenix.planet.dto.product.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.phoenix.planet.dto.product.raw.Product;

@Getter
@Setter
@Builder
public class RecommendResponse {
    private List<Product> products;
}
