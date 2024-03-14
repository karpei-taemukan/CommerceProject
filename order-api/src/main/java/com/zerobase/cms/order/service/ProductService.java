package com.zerobase.cms.order.service;

import com.zerobase.cms.order.domain.product.AddProductForm;
import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.product.AddProductItemForm;
import com.zerobase.cms.order.domain.repository.ProductRepository;
import com.zerobase.cms.order.exception.ErrorCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Product addProduct(Long sellerId, AddProductForm addProductForm){
       // System.out.println("sellerID: "+sellerId);
      //  System.out.println("form: "+addProductForm.getName());
      //  System.out.println(productRepository.save(Product.of(sellerId, addProductForm)));


        Product save = productRepository.save(Product.of(sellerId, addProductForm));
        System.out.println("save: "+save);
        return save;
    }
}