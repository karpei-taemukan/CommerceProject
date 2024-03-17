package com.zerobase.cms.order.domain.redis;

import com.zerobase.cms.order.domain.product.AddProductCartForm;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("cart") // key의 prefix를 어떤 걸 쓸 지 결정
public class Cart {
    @Id
    private Long customerId;
    private List<Product> productList = new ArrayList<>();
    private List<String> messages = new ArrayList<>(); // 메세지 함


    public Cart(Long customerId){
        this.customerId = customerId;
    }


    public void addMessage(String message){
        messages.add(message);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Product{
        private Long id;
        private Long sellerId;
        private String name;
        private String description;
        private List<ProductItem> items = new ArrayList<>();


        public static Product from(
                AddProductCartForm form
        ){
            return Product.builder()
                    .id(form.getId())
                    .sellerId(form.getSellerId())
                    .name(form.getName())
                    .description(form.getDescription())
                    .items(
                            form.getItems().stream()
                                    .map(ProductItem::from)
                                    .collect(Collectors.toList())
                    )
                    .build();
        }
    }
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductItem{
        private Long id;
        private String name;
        private Integer count;
        private Integer price;

        public static ProductItem from(
                AddProductCartForm.ProductItem productItem
        ){
            return ProductItem.builder()
                    .id(productItem.getId())
                    .name(productItem.getName())
                    .count(productItem.getCount())
                    .price(productItem.getPrice())
                    .build();
        }
    }


    public Cart clone(){
        // 깊은 복사 --> 필드의 내용물까지 하나하나 다 변화
        // 얇은 복사 --> 필드에 있는 변수를 재활용
        return new Cart(customerId, productList, messages);
    }
}
