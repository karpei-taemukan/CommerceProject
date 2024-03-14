package com.zerobase.cms.order.domain.model;

import com.zerobase.cms.order.domain.product.AddProductItemForm;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AuditOverride(forClass = BaseEntity.class)
public class ProductItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sellerId;

    @Audited
    private String name;

    @Audited
    private Integer price;

    private Integer count;

    @ManyToOne
    @JoinColumn(name = "product_id") // product_id 로 조인
    private Product product;

    // 매개변수가 1개 초과가 되면 of 라고 작성
    public static ProductItem of(Long sellerId, AddProductItemForm addProductItemForm){
        return ProductItem.builder()
                .sellerId(sellerId)
                .name(addProductItemForm.getName())
                .price(addProductItemForm.getPrice())
                .count(addProductItemForm.getCount())
                .build();
    }
}