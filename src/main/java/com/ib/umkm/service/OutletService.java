package com.ib.umkm.service;

import com.ib.umkm.common.PageResult;
import com.ib.umkm.dto.OutletDto;
import com.ib.umkm.repository.OutletRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutletService {

    private final OutletRepository outletRepository;

    public OutletService(OutletRepository outletRepository) {
        this.outletRepository = outletRepository;
    }

    public List<OutletDto> getOutlets() {
        return outletRepository.findAll();
    }

    public List<OutletDto> getCategoriesByOwnerId(Long userId) {
        return outletRepository.findByOwnerId(userId);
    }
    public List<OutletDto> getOutletsByMerchantId(Long merchantId) {
        return outletRepository.findByMerchantId(merchantId);
    }
    public PageResult<OutletDto> findPaged(int page, int size, String keyword) {
        int offset = page * size;

        List<OutletDto> outlets = outletRepository.findAll(size, offset, keyword);
        int total = outletRepository.countAll(keyword);

        return new PageResult<>(outlets, page, size, total);
    }

    public PageResult<OutletDto> findPagedByOwnerId(int page, int size, Long ownerId, String keyword) {

        int offset = page * size;

        List<OutletDto> outlets = outletRepository.findByOwnerId(size, offset, ownerId, keyword);
        int total = outletRepository.countAllByOwnerId(ownerId, keyword);

        return new PageResult<>(outlets, page, size, total);
    }

    public OutletDto getById(Long id) {
        return outletRepository.findById(id);
    }

    public void createOutlet(OutletDto request, String loginUser) {
        request.setCreatedBy(loginUser);
        outletRepository.insert(request);
    }
    public void updateOutlet(
            OutletDto request,
            String loginUser
    ) {
        request.setUpdatedBy(loginUser);
        outletRepository.update(request);
    }
}
