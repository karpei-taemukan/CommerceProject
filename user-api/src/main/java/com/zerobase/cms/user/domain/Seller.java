package com.zerobase.cms.user.domain;

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
public class Seller extends BaseEntity{
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

    public static Seller from(SignUpForm signUpForm){
        return Seller.builder()
                .email(signUpForm.getEmail().toLowerCase(Locale.ROOT))
                .password(signUpForm.getPassword())
                .birth(signUpForm.getBirth())
                .name(signUpForm.getName())
                .phone(signUpForm.getPhone())
                .verify(false)
                .build();
    }
}
