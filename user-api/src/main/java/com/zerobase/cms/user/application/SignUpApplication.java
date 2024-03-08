package com.zerobase.cms.user.application;

import com.zerobase.cms.user.client.MailgunClient;
import com.zerobase.cms.user.client.mailgun.SendMailForm;
import com.zerobase.cms.user.client.service.SignUpCustomerService;
import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.excaption.CustomException;
import com.zerobase.cms.user.excaption.ErrorCode;
import com.zerobase.cms.user.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignUpApplication {
    private final MailgunClient mailgunClient;
    private final SignUpCustomerService service;

    public String customerSignUp(SignUpForm form){
        if(service.isEmailExist(form.getEmail())){
            // 이미 가입한 이메일 있음
            throw new CustomException(ErrorCode.ALREADY_REGISTER_ACCOUNT);

        }else{
            Customer customer = service.signUp(form);

            String code = getRandomCode();

            SendMailForm verificationEmail = SendMailForm.builder()
                    .from("zerobase-test@email.com")
                    .to(form.getEmail())
                    .subject("Verification Email")
                    .text(getVerificationEmailBody(form.getEmail(), form.getName(), code))
                    .build();
            mailgunClient.sendEmail(verificationEmail);
            //log.info("Send Email reslut: "+mailgunClient.sendEmail(verificationEmail).getBody());
            service.ChangeCustomerValidateEmail(customer.getId(), code);
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

    private String getVerificationEmailBody(String email, String name, String code){
        StringBuilder sb = new StringBuilder();
        return sb.append("Hello ").append(name)
                .append("! Please Click Link for verification. \n")
                .append("http://localhost:8080/signup/verify/customer?email=")
                .append(email)
                .append("&code=")
                .append(code).toString();
    }

    //##########################################################################################

    public void customerVerify(String email, String code){
        service.verifyEmail(email,code);
    }
}
