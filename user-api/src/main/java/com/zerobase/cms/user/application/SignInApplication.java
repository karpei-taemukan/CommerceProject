package com.zerobase.cms.user.application;

import com.zerobase.cms.user.client.service.customer.CustomerService;
import com.zerobase.cms.user.client.service.seller.SellerService;
import com.zerobase.cms.user.domain.SignInForm;
import com.zerobase.cms.user.domain.Customer;
import com.zerobase.cms.user.domain.Seller;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import com.zerobase.domain.domain.common.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignInApplication {
    private final CustomerService customerService;
    private final SellerService sellerService;
    private final JwtAuthenticationProvider provider;
    public String customerLoginToken(SignInForm signInForm){
        // 1. 로그인 가능 여부
        Customer validCustomer
                = customerService.findValidCustomer(signInForm.getEmail(), signInForm.getPassword())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_CHECK_FAIL));
        // 2. 토큰 발행 후 토큰을 response
        return provider.createToken(validCustomer.getEmail(), validCustomer.getId(), UserType.CUSTOMER);
    }

    public String sellerLoginToken(SignInForm signInForm) {
        // 1. 로그인 가능 여부
        Seller validSeller =
                sellerService.findValidSeller(signInForm.getEmail(), signInForm.getPassword())
                        .orElseThrow(()->new CustomException(ErrorCode.LOGIN_CHECK_FAIL));

        // 2. 토큰 발행 후 토큰을 response
        return provider.createToken(validSeller.getEmail(), validSeller.getId(), UserType.SELLER);
    }
}