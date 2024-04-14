package com.zerobase.cms.order.service;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.repository.ProductItemRepository;
import com.zerobase.cms.order.domain.repository.ProductRepository;
import com.zerobase.cms.order.exception.CustomException;
import com.zerobase.cms.order.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchService {
    private final ProductRepository productRepository;
    private final ProductItemRepository ProductItemRepository;

    public List<Product> getListByProductId(
            List<Long> productIds
    ){
        return productRepository.findAllById(productIds);
    }

    public Product getByProductId(
            Long productId
    ){
        return productRepository.findWithProductItemsById(productId)
                .orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_PRODUCT));
    }
    public List<Product> searchByName(String name){
        return productRepository.searchByName(name);
    }

    public List<ProductItem> searchById(
            Long productId
    ){
        /*return productRepository.findWithProductItemsById(productId)
                .orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_PRODUCT));*/

        return ProductItemRepository.searchByName(productId);
    }
}
