package com.zerobase.cms.user.service;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.product.AddProductForm;
import com.zerobase.cms.order.domain.product.AddProductItemForm;
import com.zerobase.cms.order.domain.repository.ProductRepository;
import com.zerobase.cms.order.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
//@SpringBootTest(classes = ProductService.class)
class ProductServiceTest {
    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;


    @Test
        void Add_Product(){
            //given
        List<ProductItem> productItems = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            productItems.add(
                    ProductItem.builder()
                            .count(1000)
                            .id(1L)
                            .name("NIKE"+i)
                            .price(10000)
                            .build()
            );
        }

        Product product = Product.builder()
                    .sellerId(1L)
                    .name("NIKE")
                    .description("Shoes")
                    .id(1L)
                    .productItems(productItems)
                    .build();

        given(productRepository.findWithProductItemsById(anyLong()))
                .willReturn(Optional.of(product));

        //when
        List<AddProductItemForm> itemForms = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            itemForms.add(
                    AddProductItemForm.builder()
                            .productId(1L)
                            .price(10000)
                            .count(3)
                            .name("NIKE"+i)
                            .build()
            );
        }
        AddProductForm form =
                AddProductForm.builder()
                .name("NIKE")
                .description("Shoes")
                .items(itemForms)
                .build();


        Product results = productService.addProduct(1L, form);
        Product result = productRepository.findWithProductItemsById(product.getId()).get();
        //then
        System.out.println(results);
        System.out.println("result: "
                +"\nname: "+ result.getName() + " "
                +"\nDescription: "+ result.getDescription()+ " "
                +"\nSellerId: "+ result.getSellerId()+ " "
                +"\nModifiedAt: "+ result.getModifiedAt()+ " "
                +"\nCreatedAt: "+ result.getCreatedAt() + " "
                +"\nProductItems: "+ result.getProductItems());
      assertNotNull(result);
      assertEquals(result.getProductItems().size(), 3);
      assertEquals("NIKE", result.getName());
    }

/*@SpringBootTest(classes = ProductService.class)
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
        //Product result = productRepository.findWithProductItemById(product.getId()).get();

        Product result = productRepository.findById(product.getId()).get();

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
    }*/
}