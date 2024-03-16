package com.zerobase.cms.order.service;

import com.zerobase.cms.order.client.RedisClient;
import com.zerobase.cms.order.domain.product.AddProductCartForm;
import com.zerobase.cms.order.domain.redis.Cart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {

    private final RedisClient redisClient; // redis 사용을 위함


    // ##########################################################################

    public Cart getCart(Long customerId){
        return redisClient.get(customerId, Cart.class);
    }

    // ##########################################################################

    public Cart addCart(
            Long customerId,
            AddProductCartForm form
    ){
        Cart cart = redisClient.get(customerId, Cart.class);

        // 장바구니가 빈 경우
        if(cart == null){
            cart = new Cart();
            cart.setCustomerId(customerId);
        }


        Optional<Cart.Product> productOptional = cart.getProductList().stream()
                .filter(product -> product.getId().equals(form.getId()))
                .findFirst();


        // 장바구니에 물건이 있는 경우
        if(productOptional.isPresent()){
            Cart.Product redisProduct = productOptional.get();

            List<Cart.ProductItem> items = form.getItems().stream()
                    .map(Cart.ProductItem::from)
                    .toList(); // 요청한 상품 옵션

            // 검색이 용이하게 Map에 저장
            Map<Long, Cart.ProductItem> redisItemMap = redisProduct.getItems()
                    .stream().collect(Collectors.toMap(Cart.ProductItem::getId, item->item));


                //  장바구니 안 물건의 이름과 장바구니에 추가하려는 물건의 이름이 다른 경우
            if(!redisProduct.getName().equals(form.getName())){
                cart.addMessage(redisProduct.getName() + "의 정보가 변경됨");
            }


            for (Cart.ProductItem item : items){
                Cart.ProductItem redisItem = redisItemMap.get(item.getId());

                // 장바구니의 물건의 옵션이 없을 경우
                if(redisItem == null){
                    redisProduct.getItems().add(item);

                }else{
                    // 장바구니 안 물건의 가격과 장바구니에 추가할 물건의 가격이 일치하지 않는 경우
                    if(!redisItem.getPrice().equals(item.getPrice())) {
                        cart.addMessage(redisItem.getName() + " " + item.getName() + "의 가격이 변경");
                    }
                    redisItem.setCount(redisItem.getCount()+item.getCount());
                }
            }
            redisClient.put(customerId, cart);

            return cart;

            // 장바구니에 물건이 없는 경우
        }else{
            Cart.Product product = Cart.Product.from(form);
            cart.getProductList().add(product);

            redisClient.put(customerId, cart);
            return cart;
        }
    }
}