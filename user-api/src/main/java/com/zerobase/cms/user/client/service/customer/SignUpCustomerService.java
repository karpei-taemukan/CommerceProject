package com.zerobase.cms.user.client.service.customer;

import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.Customer;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import com.zerobase.cms.user.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SignUpCustomerService {
    private final CustomerRepository customerRepository;
    public Customer signUp(SignUpForm form){
       return customerRepository.save(Customer.from(form));
    }

    public boolean isEmailExist(String email){
        return customerRepository.findByEmail(email.toLowerCase(Locale.ROOT)).isPresent();
    }


    /*
    *
    * */
    @Transactional
    public LocalDateTime ChangeCustomerValidateEmail(Long customerId, String verificationCode){
        Optional<Customer> customerOptional
                = customerRepository.findById(customerId);
        if(customerOptional.isPresent()){
            Customer customer = customerOptional.get();

            customer.setVerificationCode(verificationCode);
            customer.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));
            return customer.getVerifyExpiredAt();
            /*
             * 메서드의 반환 값이 전혀 사용되지 않지만, 리턴문을 쓴 이유
             * -> 리턴문을 써야 조건문을 빠져나와 에러로 던지지 않기때문이다
             * */
        }
        throw new CustomException(ErrorCode.NOT_FOUND_USER);
    }

    //##########################################################################################

    @Transactional
    public void verifyEmail(String email, String code){
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (!Objects.equals(customer.getVerificationCode(), code)) {
            throw new CustomException(ErrorCode.WRONG_VERIFICATION);
        }

        if(customer.isVerify()){
            throw new CustomException(ErrorCode.ALREADY_VERIFY);
        }

        if (customer.getVerifyExpiredAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.EXPIRE_CODE);
        }

        customer.setVerify(true);
        customerRepository.save(customer);
    }
}