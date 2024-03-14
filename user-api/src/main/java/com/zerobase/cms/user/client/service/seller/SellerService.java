package com.zerobase.cms.user.client.service.seller;

import com.zerobase.cms.user.domain.Seller;
import com.zerobase.cms.user.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SellerService {
    private final SellerRepository sellerRepository;

    public Optional<Seller> findByIdAndEmail(Long id, String email){
        return sellerRepository.findByIdAndEmail(id,email);
    }

    public Optional<Seller> findValidSeller(String email, String password){
        return sellerRepository.findByEmailAndPasswordAndVerifyIsTrue(email, password);
    }

}
