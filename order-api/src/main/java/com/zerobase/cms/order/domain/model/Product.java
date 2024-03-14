package com.zerobase.cms.order.domain.model;

import com.zerobase.cms.order.domain.product.AddProductForm;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditOverride(forClass = BaseEntity.class)

// 실시간으로 바뀌는 내용들을 엔티디에 실시간으로 저장
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sellerId;

    private String name;

    private String description;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private List<ProductItem> productItems = new ArrayList<>();

    // 매개변수가 1개 초과가 되면 of 라고 작성
    public static Product of(Long sellerId, AddProductForm addProductForm){

        return Product.builder()
                .sellerId(sellerId)
                .name(addProductForm.getName())
                .description(addProductForm.getDescription())
                .productItems(addProductForm.getItems()
                        .stream()
                        .map(piFrom -> ProductItem.of(sellerId, piFrom))
                        .collect(Collectors.toList()))
                .build();
    }
}