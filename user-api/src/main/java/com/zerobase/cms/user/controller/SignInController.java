package com.zerobase.cms.user.controller;

import com.zerobase.cms.user.application.SignInApplication;
import com.zerobase.cms.user.domain.SignInForm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/signin")
public class SignInController {
    private final SignInApplication signInApplication;
    @PostMapping("/customer")
    public ResponseEntity<String> signInCustomer(@RequestBody SignInForm signInForm){
        return ResponseEntity.ok(signInApplication.customerLoginToken(signInForm));
    }

    @PostMapping("/seller")
    public ResponseEntity<String> signInSeller(@RequestBody SignInForm signInForm){
        return ResponseEntity.ok(signInApplication.sellerLoginToken(signInForm));
    }
}