package com.zerobase.cms.order.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.model.QProductItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductItemRepositoryImpl implements ProductItemRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public List<ProductItem> searchByName(Long id) {
        QProductItem productItem = QProductItem.productItem;
        return jpaQueryFactory.selectFrom(productItem)
                .where(productItem.id.eq(id))
                .fetch();
    }
}
