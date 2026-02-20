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
    public List<MerchantDto> getMerchantsByOwnerId(Long userId) {
        return merchantRepository.findByOwnerId(userId);
    }

    public MerchantDto getById(Long id) {
        return merchantRepository.findById(id);
    }
    public void createMerchant(MerchantDto request, String loginUser) {
        request.setCreatedBy(loginUser);
        merchantRepository.insert(request);

        //insert into
    }
    public void updateMerchant(
            MerchantDto request,
            String loginUser
    ) {
        request.setUpdatedBy(loginUser);
        merchantRepository.update(request);

    }
}
