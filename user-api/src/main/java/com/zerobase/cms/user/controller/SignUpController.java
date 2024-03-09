package com.zerobase.cms.user.controller;

import com.zerobase.cms.user.application.SignUpApplication;
import com.zerobase.cms.user.domain.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
public class SignUpController {

    private final SignUpApplication signUpApplication;

    @PostMapping("/customer")
    public ResponseEntity<String> customerSignUp(@RequestBody SignUpForm form){
        return ResponseEntity.ok(signUpApplication.customerSignUp(form));
    }

    @GetMapping("/verify/customer")
    public ResponseEntity<String> verifyCustomer(
            @RequestParam(name = "email") String email, @RequestParam(name = "code") String code){
        signUpApplication.customerVerify(email,code);
        return ResponseEntity.ok("인증이 완료되었습니다");
    }

    @PostMapping("/seller")
    public ResponseEntity<String> sellerSignUp(@RequestBody SignUpForm form){
        return ResponseEntity.ok(signUpApplication.sellerSignUp(form));
    }

    @GetMapping("/verify/seller")
    public ResponseEntity<String> verifySeller(
            @RequestParam(name = "email") String email,
            @RequestParam(name = "code") String code
    ){
    signUpApplication.sellerVerify(email, code);
    return ResponseEntity.ok("인증이 완료되었습니다");
    }
}
