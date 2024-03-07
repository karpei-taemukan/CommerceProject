package com.zerobase.cms.user.client.service;

import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Customer;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SignUpCustomerServiceTest {
    @Autowired
    private SignUpCustomerService service;

    @Test
        void signUp(){
        SignUpForm form = SignUpForm.builder()
                .name("qwe")
                .birth(LocalDate.now())
                .password("1")
                .email("zxc@dfg.com")
                .phone("01000000000")
                .build();
        Customer c = service.signUp(form);
        assertNotNull(c.getModifiedAt());
        assertNotNull(c.getId());
    }
}