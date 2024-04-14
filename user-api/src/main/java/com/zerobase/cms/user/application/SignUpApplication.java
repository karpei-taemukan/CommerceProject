package com.zerobase.cms.user.application;

import com.zerobase.cms.user.client.MailgunClient;
import com.zerobase.cms.user.client.mailgun.SendMailForm;
import com.zerobase.cms.user.client.service.customer.SignUpCustomerService;
import com.zerobase.cms.user.client.service.seller.SignUpSellerService;
import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.Customer;
import com.zerobase.cms.user.domain.Seller;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignUpApplication {
    private final MailgunClient mailgunClient;
    private final SignUpCustomerService signUpCustomerService;
    private final SignUpSellerService signUpSellerService;

    public String customerSignUp(SignUpForm form){
        if(signUpCustomerService.isEmailExist(form.getEmail())){
            // 이미 가입한 이메일 있음
            throw new CustomException(ErrorCode.ALREADY_REGISTER_ACCOUNT);

        }else{
            Customer customer = signUpCustomerService.signUp(form);

            String code = getRandomCode();

            SendMailForm verificationEmail = SendMailForm.builder()
                    .from("zerobase-test@email.com")
                    .to(form.getEmail())
                    .subject("Verification Email")
                    .text(getVerificationEmailBody(form.getEmail(), form.getName(), "customer" ,code))
                    .build();
            mailgunClient.sendEmail(verificationEmail);
            //log.info("Send Email reslut: "+mailgunClient.sendEmail(verificationEmail).getBody());
            signUpCustomerService.ChangeCustomerValidateEmail(customer.getId(), code);
            return "회원 가입에 성공";
        }
    }

    public String sellerSignUp(SignUpForm form){
        if(signUpSellerService.isEmailExist(form.getEmail())){
            // 이미 가입한 이메일 있음
            throw new CustomException(ErrorCode.ALREADY_REGISTER_ACCOUNT);
        }else{
            Seller seller = signUpSellerService.signUp(form);

            String code = getRandomCode();

            SendMailForm verificationEmail = SendMailForm.builder()
                    .from("zerobase-test@email.com")
                    .to(form.getEmail())
                    .subject("Verification Email")
                    .text(getVerificationEmailBody(form.getEmail(), form.getName(), "seller" ,code))
                    .build();
            mailgunClient.sendEmail(verificationEmail);
            //log.info("Send Email reslut: "+mailgunClient.sendEmail(verificationEmail).getBody());
            signUpSellerService.ChangeSellerValidateEmail(seller.getId(), code);
            return "회원 가입에 성공";
        }
    }

    /*
    * 이메일을 위한 템플릿
    * */

    private String getRandomCode() {
        return RandomStringUtils.random(10,true,true);
        // 10 자리의 문자와 숫자가 섞인 랜덤문자
    }

    private String getVerificationEmailBody(String email, String name, String type, String code){
        StringBuilder sb = new StringBuilder();

        return sb.append("Hello ").append(name)
                .append("! Please Click Link for verification. \n")
                .append("http://localhost:8080/signup")
                .append("/verify/")
                .append(type)
                .append("?email=")
                .append(email)
                .append("&code=")
                .append(code)
                .toString();
    }

    //##########################################################################################

    public void customerVerify(String email, String code){
        signUpCustomerService.verifyEmail(email,code);
    }
    public void sellerVerify(String email, String code){
        signUpSellerService.verifyEmail(email,code);
    }
}
