package com.zerobase.cms.user.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 거래 내역
public class CustomerBalanceHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = Customer.class, fetch = FetchType.LAZY)
    private Customer customer;

    // 변경된 돈
    private Integer changeMoney;

    // 해당 시점 잔액
    private Integer currentMoney;

    private String fromMessage;

    private String description;
}
