package com.zerobase.cms.order.application;

import com.zerobase.cms.order.client.MailgunClient;
import com.zerobase.cms.order.client.UserClient;
import com.zerobase.cms.order.client.mailgun.SendMailForm;
import com.zerobase.cms.order.client.user.ChangeBalanceForm;
import com.zerobase.cms.order.client.user.CustomDto;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.cms.order.exception.CustomException;
import com.zerobase.cms.order.exception.ErrorCode;
import com.zerobase.cms.order.service.CartService;
import com.zerobase.cms.order.service.ProductItemService;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
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

    private final JwtAuthenticationProvider provider;
    private final MailgunClient mailgunClient;
    private final CartService cartService;

    @Transactional
    public void order(String token, Cart cart){

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
                .message("ORDER")
                .money(-totalPrice)
                .build();

        System.out.println(form.getFrom());
        System.out.println(form.getMoney());
        System.out.println(form.getMessage());

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

        // 주문내역 이메일로 발송

        String customerEmail = provider.getUserVo(token).getEmail();

        System.out.println("EMAIL: "+customerEmail);

        StringBuilder sb = new StringBuilder();

        List<String> productName = cart.getProductList().stream().map(Cart.Product::getName).toList();

        System.out.println("productName  " + productName);

        List<String> productDesc = cart.getProductList().stream().map(Cart.Product::getDescription).toList();

        System.out.println("productDesc  " + productDesc);

        List<List<Cart.ProductItem>> productOpt = cart.getProductList().stream().map(product -> new ArrayList<>(product.getItems()).reversed()).toList();

        System.out.println("productOpt  " + productDesc);

        String mailText = sb.append("productName \n")
                .append(productName.getFirst())
                .append("productDescription \n")
                .append(productDesc.getFirst())
                .append("productItems \n")
                .append(productOpt.getFirst())
                .toString();

        SendMailForm sendMailForm = SendMailForm.builder()
                .from("zerobase-test@email.com")
                .to(customerEmail)
                .subject("ORDER HISTORY")
                .text(mailText)
                .build();

        mailgunClient.sendEmail(sendMailForm);

        // 주문 완료 후 카트 비우기
        cartService.putCart(cart.getCustomerId(), null);

    }


    // product 안 productItem 의 price 들의 합 구하기
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