package com.zerobase.cms.order.domain.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductItemForm {
    private Long id; // 수정할 제품 옵션 id
    private Long productId; // 제품 일련 번호
    private String name;
    private Integer price;
    private Integer count;
}