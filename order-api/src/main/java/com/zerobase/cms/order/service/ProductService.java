package com.zerobase.cms.order.service;

import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.product.AddProductForm;
import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.product.AddProductItemForm;
import com.zerobase.cms.order.domain.product.UpdateProductForm;
import com.zerobase.cms.order.domain.product.UpdateProductItemForm;
import com.zerobase.cms.order.domain.repository.ProductItemRepository;
import com.zerobase.cms.order.domain.repository.ProductRepository;
import com.zerobase.cms.order.exception.CustomException;
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
    private final ProductItemRepository productItemRepository;

    @Transactional
    public Product addProduct(Long sellerId, AddProductForm addProductForm){
       // System.out.println("sellerID: "+sellerId);
      //  System.out.println("form: "+addProductForm.getName());
      //  System.out.println(productRepository.save(Product.of(sellerId, addProductForm)));


        Product save = productRepository.save(Product.of(sellerId, addProductForm));
        System.out.println("save: "+save);
        return save;
    }

    // ###########################################################################################

    @Transactional
    public Product updateProduct(
            Long sellerId,
            UpdateProductForm form
    ){
        Product product = productRepository.findBySellerIdAndId(sellerId, form.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        product.setName(form.getName());
        product.setDescription(form.getDescription());

        // 옵션이 변한것들을 한번에 처리
        for(UpdateProductItemForm itemForm : form.getItems()){
            ProductItem item = product.getProductItems().stream()
                    .filter(pi -> pi.getId().equals(itemForm.getId()))
                    .findFirst()
                    .orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_ITEM));

            item.setName(itemForm.getName());
            item.setPrice(itemForm.getPrice());
            item.setCount(itemForm.getCount());
        }

        return product;
    }

    // ###########################################################################################

    @Transactional
    public void deleteProduct(
            Long sellerId,
            Long productId
    ){
        Product product = productRepository.findBySellerIdAndId(sellerId, productId)
                .orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        productRepository.delete(product);
    }
}