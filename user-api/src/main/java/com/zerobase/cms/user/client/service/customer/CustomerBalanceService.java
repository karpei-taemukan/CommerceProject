package com.zerobase.cms.user.client.service.customer;

import com.zerobase.cms.user.domain.customer.ChangeBalanceForm;
import com.zerobase.cms.user.domain.CustomerBalanceHistory;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import com.zerobase.cms.user.repository.CustomerBalanceHistoryRepository;
import com.zerobase.cms.user.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerBalanceService {
    private final CustomerBalanceHistoryRepository customerBalanceHistoryRepository;
    private final CustomerRepository customerRepository;

    @Transactional(noRollbackFor = {CustomException.class})
    // 계좌에 돈 넣거나 빼기
    public CustomerBalanceHistory changeBalance(Long customerId, ChangeBalanceForm from)
    throws CustomException{
    CustomerBalanceHistory customerBalanceHistory
                = customerBalanceHistoryRepository
            .findFirstByCustomer_IdOrderByIdDesc(customerId)
            // 거래 내역이 없을 경우
            .orElse(CustomerBalanceHistory.builder()
                    .changeMoney(0)
                    .currentMoney(0)
                    .customer(customerRepository.findById(customerId)
                            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER)))
                    .build());

    if(customerBalanceHistory.getCurrentMoney() + from.getMoney() < 0){
        throw new CustomException(ErrorCode.NOT_ENOUGH_BALANCE);
    }

     customerBalanceHistory = CustomerBalanceHistory.builder()
             //form 에서 들어온 돈 반영
             .changeMoney(from.getMoney())
             .currentMoney(customerBalanceHistory.getCurrentMoney() + from.getMoney())
             .description(from.getMessage())
             .fromMessage(from.getFrom())
             .customer(customerBalanceHistory.getCustomer())
             .build();

    customerBalanceHistory.getCustomer().setBalance(customerBalanceHistory.getCurrentMoney());

    return customerBalanceHistoryRepository.save(customerBalanceHistory);
    }
}