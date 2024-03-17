package com.zerobase.cms.order.client.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CustomDto {
    private Long id;
    private String email;
    private Integer balance;
}
