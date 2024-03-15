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


    // 검색을 할때 제품의 옵션까지 보여주면 성능이 떨어짐

    public static ProductDto WithoutItemsfrom(Product product){
        List<ProductItemDto> items = product.getProductItems()
                .stream()
                .map(ProductItemDto::from)
                .toList();
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                //.items(items)
                .build();
    }
}