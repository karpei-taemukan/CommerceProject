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


        // 홈페이지에 있는 물건의 갯수 < 추가하려는 물건의 갯수 체크
        if (cart != null && !addAble(cart, product, form)) {
            throw new CustomException(ErrorCode.ITEM_COUNT_NOT_ENOUGH);
        }

        return cartService.addCart(customerId, form);
    }

    // 장바구니에 추가할 수 있는지 체크
    private boolean addAble(Cart cart, Product product, AddProductCartForm form) {

        // 장바구니에 있는 물건이랑 추가하려는 믈건과 일치하는 물건 찾기
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
                formItem -> {
                    Integer cartCount = cartItemCountMap.get(formItem.getId());
                    if(cartCount == null){
                        cartCount = 0;
                    }

                    Integer currentCount = currentItemCountMap.get(formItem.getId());

                    // 만약 20개의 물건을 추가하려고하는데 홈페이지에는 10개의 물건 밖에 안 올라온 경우를 체크
                    // 현재 추가하려는 물건의 갯수 + 장바구니에 있는 물건 갯수 > 홈페이지에 올라온 물건 갯수
                    return formItem.getCount() + cartCount > currentCount; // 이 조건을 만족하면 안됨
                    // 무조건 장바구니 물건 + 현재 추가하려는 물건 갯수 보다 홈페이지에 올라온 물건의 갯수가 많아야한다
                }
        );
    }


    // ##########################################################################################################


    // 장바구니에 상품 추가 후, 상품의 가격이나 수량이 변동된 경우
    public Cart getCart(Long customerId){

        Cart cart = refreshCart(cartService.getCart(customerId));

        // 장바구니가 비어서 물건이 없는 경우
        // 빈 장바구니 리턴
        if(cart.getProductList().isEmpty()){
            System.out.println("CART NULL");
            cart.setCustomerId(customerId);
            cart.addMessage("빈 카트");
            return cart;
        }

        cartService.putCart(cart.getCustomerId(), cart);
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
    protected Cart refreshCart(Cart cart){

        System.out.println("GET CART "+ cart.getProductList());

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

                // 홈페이지에 있는 가격과 장바구니에 있는 가격이 서로 다른 경우
                if(!cartProductItem.getPrice().equals(productItemMap.get(cartProductItem.getId()).getPrice()))
                {
                    isPriceChanged = true;

                    // 장바구니에 있는 물건 가격 업데이트
                    cartProductItem.setPrice(pi.getPrice());
                }

                // 홈페이지에 있는 물건의 갯수가 변경된경우(장바구니에 업데이트 안된 상황)
                if(cartProductItem.getCount() > productItemMap.get(cartProductItem.getId()).getCount())
                {
                    isCountNotEnough = true;
                    // 장바구니에 있는 물건 수량 업데이트 --> 홈페이지에 있는 물건의 갯수로 업데이트 즉, 최대 치로 변경
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
            // 만약 상품의 옵션이 없다면 그 상품은 의미가 없으므로 삭제한다
            if(cartProduct.getItems().isEmpty()){
                cart.getProductList().remove(cartProduct);
                i--;
                cart.addMessage(cartProduct.getName() + " 물건 옵션이 모두 없어져 구매 불가");
                continue;
            } else if (!tmpMessages.isEmpty()) { // 변동사항 메세지 생겼을때마다 전송
                StringBuilder builder = new StringBuilder();
                builder.append(cartProduct.getName()).append(" 상품의 변동 사항: ");

                for (String msg : tmpMessages){
                    builder.append(msg);
                    builder.append(", ");
                }
                cart.addMessage(builder.toString());
            }
        }

        return cart;
    }

    // ##########################################################################################################

    public void clearCart(Long customerId){
        cartService.putCart(customerId, null);
    }

    // ##########################################################################################################

    // 사용자 입장
    // 장바구니 물건의 갯수를 수정한다거나
    // 장바구니 물건 옵션 삭제
    public Cart updateCart(Long customerId, Cart cart){
        cartService.putCart(customerId, cart);
        return getCart(customerId);
    }
}