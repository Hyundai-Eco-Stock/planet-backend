package org.phoenix.planet.dto.raffle;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductImage {
    private Long imageId;
    private String imageUrl;
    private Integer sortOrder;
    private String altText;

}