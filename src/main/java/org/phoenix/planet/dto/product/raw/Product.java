package org.phoenix.planet.dto.product.raw;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    private long id;

    private long categoryId;

    private long brandId;

    private String name;

    private Long price;

    private Integer quantity;

    private String ecoDealStatus;

    private int salePercent;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}