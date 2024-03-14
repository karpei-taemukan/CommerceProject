package com.zerobase.cms.order.domain.model;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private List<ProductItemDto> items;

    public static ProductDto from(Product product){
        List<ProductItemDto> items = product.getProductItems()
                .stream()
                .map(ProductItemDto::from)
                .toList();
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .items(items)
                .build();
    }
}