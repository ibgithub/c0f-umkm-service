package com.ib.umkm.service;

import com.ib.umkm.dto.MerchantDto;
import com.ib.umkm.repository.MerchantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantService {

    private final MerchantRepository merchantRepository;

    public MerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    public List<MerchantDto> getAllMerchants() {
        return merchantRepository.findAll();
    }

    public MerchantDto getById(Long id) {
        return merchantRepository.findById(id);
    }
    public void createMerchant(MerchantDto request, String loginUser) {
        request.setCreatedBy(loginUser);
        merchantRepository.insert(request);
    }
    public void updateMerchant(
            MerchantDto request,
            String loginUser
    ) {
        Long targetUserId = request.getId();
        request.setUpdatedBy(loginUser);
        merchantRepository.update(request);
    }
}
