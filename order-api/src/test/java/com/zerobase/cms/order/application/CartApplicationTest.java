package com.zerobase.cms.order.application;

import com.zerobase.cms.config.TestRedisConfig;
import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.product.AddProductCartForm;
import com.zerobase.cms.order.domain.product.AddProductForm;
import com.zerobase.cms.order.domain.product.AddProductItemForm;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.cms.order.domain.repository.ProductRepository;
import com.zerobase.cms.order.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(classes = TestRedisConfig.class)
class CartApplicationTest {
    @Autowired
    private CartApplication cartApplication;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @Test
        void ADD_TEST(){
        Product product = add_product();

        Product result = productRepository.findWithProductItemsById(product.getId()).get();

        assertNotNull(result);

        assertEquals(result.getName(), "NIKE");
        assertEquals(result.getDescription(), "Shoes");

        assertEquals(result.getProductItems().size(), 3);
        assertEquals(result.getProductItems().getFirst().getName(), "NIKE0");
        assertEquals(result.getProductItems().getFirst().getPrice(), 10000);
        //assertEquals(result.getProductItems().getFirst().getCount(), 1);

        // ##########################################################################################
        // CartApplication Test 시작

        Long customerId = 100L;

        cartApplication.clearCart(customerId);

        // 가격을 임의로 20000원으로 바꿈
        Cart cart = cartApplication.addCart(customerId, makeProductCartForm(result));

        // 장바구니에 처음 담는 경우라서 가격을 변동해도 가격변동 메세지는 없다
        assertEquals(cart.getMessages().size(), 0);

        cart = cartApplication.getCart(customerId);
        assertEquals(cart.getMessages().size(), 1);
        assertEquals(cart.getProductList().getFirst().getName(), "NIKE");
        assertEquals(cart.getProductList().getFirst().getItems().getFirst().getName(), "NIKE0");
        assertEquals(cart.getCustomerId(), 100);
        assertNotNull(cart.getMessages());
        assertNotNull(cart.getProductList().getFirst().getItems());
    }


    AddProductCartForm makeProductCartForm(Product p){
       AddProductCartForm.ProductItem productItem =
               AddProductCartForm.ProductItem.builder()
                       .id(p.getProductItems().getFirst().getId())
                       .name(p.getProductItems().getFirst().getName())
                       .count(5)
                       .price(20000)
               .build();

      return AddProductCartForm.builder()
               .id(p.getId())
               .sellerId(p.getSellerId())
               .description(p.getDescription())
               .name(p.getName())
               .items(List.of(productItem))
               .build();
    }


    Product add_product(){
        Long sellerId = 1L;

        AddProductForm form = makeProductForm("NIKE", "Shoes", 3);

        return productService.addProduct(sellerId, form);
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
                .count(10)
                .price(10000)
                .productId(productId)
                .name(name)
                .build();
    }
}