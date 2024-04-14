package com.zerobase.cms.user.service;

import com.zerobase.cms.order.OrderMainApplication;
import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.product.AddProductForm;
import com.zerobase.cms.order.domain.product.AddProductItemForm;
import com.zerobase.cms.order.domain.repository.ProductRepository;
import com.zerobase.cms.order.service.ProductService;
import jakarta.persistence.EntityListeners;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;





@SpringBootTest(classes = OrderMainApplication.class)
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
        void ADD_PRODUCT_TEST(){
        //given
        Long sellerId = 1L;

        AddProductForm form = makeProductForm("NIKE", "Shoes", 3);

        //when
        Product product = productService.addProduct(sellerId, form);


        //then
        Product result = productRepository.findWithProductItemsById(product.getId()).get();

        //Product result = productRepository.findById(product.getId()).get();

        assertNotNull(result);
    }

    private static AddProductForm makeProductForm
            (String name, String description, int itemCount){
        List<AddProductItemForm> itemForms = new ArrayList<>();

        for (int i = 0; i < itemCount; i++) {
            itemForms.add(makeProductItemForm(null, name+i));
        }

        return AddProductForm.builder()
                .name(name)
                .description(description)
                .items(itemForms)
                .build();
    }

    private static AddProductItemForm makeProductItemForm(Long productId, String name){
        return AddProductItemForm.builder()
                .count(1)
                .price(10000)
                .productId(productId)
                .name(name)
                .build();
    }
}