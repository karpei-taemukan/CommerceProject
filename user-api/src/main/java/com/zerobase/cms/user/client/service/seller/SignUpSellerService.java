package com.zerobase.cms.user.client.service.seller;

import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Seller;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import com.zerobase.cms.user.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SignUpSellerService {
    private final SellerRepository sellerRepository;

    public Seller signUp(SignUpForm form){
        return sellerRepository.save(Seller.from(form));
    }

    public boolean isEmailExist(String email){
        return sellerRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public void verifyEmail(String email, String code){
        Seller seller = sellerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (!Objects.equals(seller.getVerificationCode(), code)) {
            throw new CustomException(ErrorCode.WRONG_VERIFICATION);
        }
        if(seller.isVerify()){
            throw new CustomException(ErrorCode.ALREADY_VERIFY);
        }
        if(seller.getVerifyExpiredAt().isBefore(LocalDateTime.now())){
            throw new CustomException(ErrorCode.EXPIRE_CODE);
        }

        seller.setVerify(true);
        sellerRepository.save(seller);
    }

    @Transactional
    public LocalDateTime ChangeSellerValidateEmail(Long sellerId, String verificationCode){
        Optional<Seller> sellerOptional =
                sellerRepository.findById(sellerId);
        if(sellerOptional.isPresent()){
            Seller seller = sellerOptional.get();
            seller.setVerificationCode(verificationCode);
            seller.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));
            return seller.getVerifyExpiredAt();
            /*
            * 메서드의 반환 값이 전혀 사용되지 않지만, 리턴문을 쓴 이유
            * -> 리턴문을 써야 조건문을 빠져나와 에러로 던지지 않기때문이다
            * */
        }

        throw new CustomException(ErrorCode.NOT_FOUND_USER);
    }
}