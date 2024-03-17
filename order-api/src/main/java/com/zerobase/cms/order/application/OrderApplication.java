package com.zerobase.cms.order.application;

import com.zerobase.cms.order.client.UserClient;
import com.zerobase.cms.order.client.user.ChangeBalanceForm;
import com.zerobase.cms.order.client.user.CustomDto;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.cms.order.exception.CustomException;
import com.zerobase.cms.order.exception.ErrorCode;
import com.zerobase.cms.order.service.ProductItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class OrderApplication {
    /* 결제를 위해 필요한것

    1. 물건들이 전부 주문 가능한 상태인지 확인
    2. 가격 변동이 있었는지에 대해 확인
    3. 고객의 돈이 충분한지 확인
    4. 결제, 상품의 재고 관리
    */

    private final CartApplication cartApplication;
    private final UserClient userClient;
    private final ProductItemService productItemService;
    @Transactional
    public void order(String token, Cart cart){
        // 주문을 하면 장바구니 삭제
        Cart orderCart = cartApplication.refreshCart(cart);

        // 중간에 홈페이지에 올라온 물건의 가격이나 수량이 변한 경우 에러 메세지가 있음
        if(!orderCart.getMessages().isEmpty()){
            throw new CustomException(ErrorCode.ORDER_FAIL_CHOICE_CART);
        }


        CustomDto customerInfo = userClient.getCustomerInfo(token).getBody();

        // 고객의 잔액 < 고객이 주문하려는 물건 가격 * 물건 갯수 보다
        // 고객이 정가보다 더 싼 가격으로 물건을 구매하게됨
        int totalPrice = getTotalPrice(cart);
        if(Objects.requireNonNull(customerInfo).getBalance() < totalPrice){
            throw new CustomException(ErrorCode.ORDER_FAIL_NO_MONEY);
        }

        //----------------------------------------------------------------------------------
        // 주문

        // 결제할 양식 작성
        ChangeBalanceForm form = ChangeBalanceForm.builder()
                .from("USER")
                .message("Order")
                .money(-totalPrice)
                .build();
        userClient.changeBalance(token, form);

        //----------------------------------------------------------------------------------

        // 주문할 물건의 수량과 전체 가격을 업데이트
        for (Cart.Product product : orderCart.getProductList()){
            for (Cart.ProductItem cartItem : product.getItems()){
                ProductItem productItem =
                        productItemService.getProductItem(cartItem.getId());
                // 주문한 물건 갯수만큼 홈페이지의 물건 갯수 차감
                productItem.setCount(productItem.getCount() - cartItem.getCount());
            }
        }
    }


    private Integer getTotalPrice(Cart cart){

        return cart.getProductList().stream().flatMapToInt(
                product ->
                        product.getItems().stream().flatMapToInt(
                                productItem ->
                                        IntStream.of(productItem.getPrice() * productItem.getCount())
                )
        ).sum();
    }
}