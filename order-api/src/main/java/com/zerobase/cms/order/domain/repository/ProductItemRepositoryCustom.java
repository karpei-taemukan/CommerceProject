package com.zerobase.cms.order.domain.repository;

import com.zerobase.cms.order.domain.model.ProductItem;

import java.util.List;

public interface ProductItemRepositoryCustom {
    List<ProductItem> searchByName(Long id);
}
