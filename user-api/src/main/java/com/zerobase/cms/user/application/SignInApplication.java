package com.zerobase.cms.user.application;

import com.zerobase.cms.user.client.service.CustomerService;
import com.zerobase.cms.user.domain.SignInForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.excaption.CustomException;
import com.zerobase.cms.user.excaption.ErrorCode;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import com.zerobase.domain.domain.common.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignInApplication {
    private final CustomerService customerService;
    private  final JwtAuthenticationProvider provider;
    public String customerLoginToken(SignInForm signInForm){
        // 1. 로그인 가능 여부
        Customer validCustomer
                = customerService.findValidCustomer(signInForm.getEmail(), signInForm.getPassword())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_CHECK_FAIL));
        // 2. 토큰 발행 후 토큰을 response
        return provider.createToken(validCustomer.getEmail(), validCustomer.getId(), UserType.CUSTOMER);
    }
}