package com.zerobase.cms.user.domain.model;

import com.zerobase.cms.user.domain.SignUpForm;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.AuditOverride;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AuditOverride(forClass = BaseEntity.class)
// Customer 클래스가 업데이트될때마다
// 자동으로 BaseEntity 클래스의
// createdAt, modifiedAt 업데이트
public class Customer extends BaseEntity{
    @Id
    @Column(name = "id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    private String name;
    private String password;
    private String phone;
    private LocalDate birth;

    private LocalDateTime verifyExpiredAt;
    private String verificationCode;
    private boolean verify;

    public static Customer from(SignUpForm signUpForm){
        return Customer.builder()
                .email(signUpForm.getEmail().toLowerCase(Locale.ROOT))
                .password(signUpForm.getPassword())
                .birth(signUpForm.getBirth())
                .name(signUpForm.getName())
                .phone(signUpForm.getPhone())
                .verify(false)
                .build();
    }
}
