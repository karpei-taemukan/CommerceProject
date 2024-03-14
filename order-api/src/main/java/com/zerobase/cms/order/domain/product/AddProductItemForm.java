package com.zerobase.cms.order.domain.product;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddProductItemForm {
    private Long productId;
    private String name;
    private Integer price;
    private Integer count;
}