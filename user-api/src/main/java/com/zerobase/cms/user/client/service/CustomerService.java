package com.zerobase.cms.user.client.service;

import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    // 해당 필터에 맞는 조건이 없다면 Optional 로 리턴
    public Optional<Customer> findValidCustomer(String email, String password){
       return customerRepository.findByEmail(email).stream().filter(
                customer -> customer.getPassword().equals(password) && customer.isVerify()
        ).findFirst();
    }

    //#################################################################################

    public Optional<Customer> findByIdAndEmail(Long id, String email){
      return customerRepository.findById(id)
              .stream().filter(customer -> customer.getEmail().equals(email))
              .findFirst();
    }

}
