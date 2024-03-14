package com.zerobase.cms.order.domain.product;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddProductForm {
    private String name;
    private String description;
    private List<AddProductItemForm> items;
}