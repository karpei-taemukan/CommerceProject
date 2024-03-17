package com.zerobase.cms.order.application;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.product.AddProductCartForm;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.cms.order.exception.CustomException;
import com.zerobase.cms.order.exception.ErrorCode;
import com.zerobase.cms.order.service.CartService;
import com.zerobase.cms.order.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.zerobase.cms.order.domain.model.QProductItem.productItem;

@Service
@RequiredArgsConstructor
@Transactional
public class CartApplication {
    private final ProductSearchService productSearchService;
    private final CartService cartService;

    // 카트에 담기전 담을 물건이 담아도 되는 물건인지 체크

    public Cart addCart(
            Long customerId,
            AddProductCartForm form
    ) {
        Product product = productSearchService.getByProductId(form.getId());

        // 추가하려는 물건이 있는 지 확인
        if (product == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);
        }

        // redis 에 저장된 장바구니 가져오기
        Cart cart = cartService.getCart(customerId);


        if (cart != null && !addAble(cart, product, form)) {
            throw new CustomException(ErrorCode.ITEM_COUNT_NOT_ENOUGH);
        }

        return cartService.addCart(customerId, form);
    }

    private boolean addAble(Cart cart, Product product, AddProductCartForm form) {

        Cart.Product cartProduct = cart.getProductList().stream()
                .filter(p -> p.getId().equals(form.getId()))
                .findFirst()
                .orElse(Cart.Product.builder()
                        .id(product.getId())
                        .items(Collections.emptyList())
                        .build());
                //.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        // 장바구니의 물건 갯수 가져오기
        Map<Long, Integer> cartItemCountMap = cartProduct.getItems().stream()
                .collect(Collectors.toMap(Cart.ProductItem::getId, Cart.ProductItem::getCount));

        // 홈페이지에 올라온 물건 갯수 가져오기
        Map<Long, Integer> currentItemCountMap = product.getProductItems().stream()
                .collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));


        return form.getItems().stream().noneMatch( // 특정 조건 한개라도 만족하면 안됨
                productItem -> {
                    Integer cartCount = cartItemCountMap.get(productItem.getId());
                    if(cartCount == null){
                        cartCount = 0;
                    }
                    Integer currentCount = currentItemCountMap.get(productItem.getId());
                    // 현재 추가하려는 물건의 갯수 + 장바구니에 있는 물건 갯수 > 홈페이지에 올라온 물건 갯수
                    return productItem.getCount() + cartCount > currentCount; // 이 조건을 만족하면 안됨
                    // 무조건 장바구니 물건 + 현재 추가하려는 물건 갯수 보다 홈페이지에 올라온 물건의 갯수가 많아야한다
                }
        );
    }

    // ##########################################################################################################



    // 장바구니에 상품 추가 후, 상품의 가격이나 수량이 변동된 경우
    public Cart getCart(Long customerId){

        Cart cart = refreshCart(cartService.getCart(customerId));
        Cart returnCart = new Cart();
        // 얇은 복사
        returnCart.setCustomerId(customerId);
        returnCart.setProductList(cart.getProductList());
        returnCart.setMessages(cart.getMessages());
        // 만약 알람이 오고 본 다음, 이미 본 메세지는 제거(스팸 제거)
        cart.setMessages(new ArrayList<>());
        cartService.putCart(customerId, cart); // redis 의 장바구니에서 메세지 제외
        return returnCart;
    }



    // 장바구니에 상품 추가 후, 상품의 가격이나 수량이 변동된 경우
    private Cart refreshCart(Cart cart){
        // 상품이나 상품의 옵션-> description, price, count 가 변경되는지 체크 그에 맞는 알람 제공
        // 상품의 수량, 가격 변경

        // 장바구니에 있는 물건과 홈페이지에 올라온 물건(장바구니랑 같은 물건)이 서로 바뀐게 없는 지 체크


        // 홈페이지에 있는 물건이 삭제되었으나 장바구니엔 고른 물건이 남아있는 경우를 방지

        // 장바구니 물건 정보 가져오기
        Map<Long, Product> productMap = productSearchService.getListByProductId(
                cart.getProductList().stream()
                        .map(Cart.Product::getId).collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        // for 문을 Product 기준이 아닌 Cart.Product 으로 돌려야
        // 홈페이지엔 없는 물건인데 장바구니엔 있는 물건이 되는 상황을 방지한다

        for (int i=0; i<cart.getProductList().size(); i++){

            Cart.Product cartProduct = cart.getProductList().get(i);

            Product p = productMap.get(cartProduct.getId());

            // for 문 중간에 홈페이지 물건이 없어지고 장바구니엔 물건이 있는 경우
            if(p == null){
                cart.getProductList().remove(cartProduct);
                i--;
                cart.addMessage(cartProduct.getName() + "이 삭제됨");
                continue;
            }

            // 검색속도 향상 (홈페이지에 있는 물건 옵션)
            Map<Long, ProductItem> productItemMap = p.getProductItems().stream()
                    .collect(Collectors.toMap(ProductItem::getId, productItem -> productItem));


            List<String> tmpMessages = new ArrayList<>();

            for (int j=0; j<cartProduct.getItems().size(); j++){

                Cart.ProductItem cartProductItem = cartProduct.getItems().get(j);

                ProductItem pi = productItemMap.get(cartProductItem.getId());

                // for 문 중간에 홈페이지 물건의 옵션이 변경되어 장바구니 물건과 옵션이 다른 경우
                if(pi == null){
                    cartProduct.getItems().remove(cartProductItem);
                    j--;
                    tmpMessages.add(cartProductItem.getName() + "이 삭제됨(옵션)");
                    continue;
                }



                boolean isPriceChanged = false, isCountNotEnough = false;

                if(!cartProductItem.getPrice().equals(productItemMap.get(cartProductItem.getId()).getPrice()))
                {
                    isPriceChanged = true;
                    // 장바구니에 있는 물건 가격 업데이트
                    cartProductItem.setPrice(pi.getPrice());
                }

                if(  // 홈페이지에 있는 물건의 갯수가 변경된경우(장바구니에 업데이트 안된 상황)
                 cartProductItem.getCount() > productItemMap.get(cartProductItem.getId()).getCount())
                {
                    isCountNotEnough = true;
                    // 장바구니에 있는 물건 수량 업데이트
                    cartProductItem.setCount(pi.getCount());
                }


                if(isPriceChanged && isCountNotEnough){
                    tmpMessages.add(cartProductItem.getName() + "의 가격 변동됨, 수량 부족함 그래서 구매 가능 최대치로 변경함");

                }else if(isPriceChanged){
                    tmpMessages.add(cartProductItem.getName() + "의 가격이 변동됨");

                }else if(isCountNotEnough){
                    tmpMessages.add(cartProductItem.getName() + "의 수량 부족함 그래서 구매 가능 최대치로 변경함");

                }

            }
            // 삭제한 물건의 옵션까지 삭제 진행
            if(cartProduct.getItems().isEmpty()){
                cart.getProductList().remove(cartProduct);
                i--;
                cart.addMessage(cartProduct.getName() + " 물건 옵션이 모두 없어져 구매 불가");
                continue;
            } else if (!tmpMessages.isEmpty()) { //
                StringBuilder builder = new StringBuilder();
                builder.append(cartProduct.getName()).append(" 상품의 변동 사항: ");

                for (String msg : tmpMessages){
                    builder.append(msg);
                    builder.append(", ");
                }
                cart.addMessage(builder.toString());
            }
        }
        cartService.putCart(cart.getCustomerId(), cart);
        return cart;
    }

    // ##########################################################################################################

    public void clearCart(Long customerId){
        cartService.putCart(customerId, null);
    }

}