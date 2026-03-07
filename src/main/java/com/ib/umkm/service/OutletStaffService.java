package com.ib.umkm.service;

import com.ib.umkm.common.PageResult;
import com.ib.umkm.dto.OutletStaffDto;
import com.ib.umkm.repository.OutletStaffRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutletStaffService {

    private final OutletStaffRepository outletStaffRepository;

    public OutletStaffService(OutletStaffRepository outletStaffRepository) {
        this.outletStaffRepository = outletStaffRepository;
    }

    public List<OutletStaffDto> getOutletStaffs() {
        return outletStaffRepository.findAll();
    }

    public List<OutletStaffDto> getOutletStaffsByOwnerId(Long userId) {
        return outletStaffRepository.findByOwnerId(userId);
    }

    public PageResult<OutletStaffDto> findPaged(int page, int size, String keyword) {
        int offset = page * size;

        List<OutletStaffDto> outletStaffs = outletStaffRepository.findAll(size, offset, keyword);
        int total = outletStaffRepository.countAll(keyword);

        return new PageResult<>(outletStaffs, page, size, total);
    }

    public PageResult<OutletStaffDto> findPagedByOwnerId(int page, int size, Long ownerId, String keyword) {

        int offset = page * size;

        List<OutletStaffDto> outletStaffs = outletStaffRepository.findByOwnerId(size, offset, ownerId, keyword);
        int total = outletStaffRepository.countAllByOwnerId(ownerId, keyword);

        return new PageResult<>(outletStaffs, page, size, total);
    }

    public OutletStaffDto getById(Long id) {
        return outletStaffRepository.findById(id);
    }

    public void createOutletStaff(OutletStaffDto request, String loginUser) {
        request.setCreatedBy(loginUser);
        outletStaffRepository.insert(request);
    }
    public void updateOutletStaff(
            OutletStaffDto request,
            String loginUser
    ) {
        request.setUpdatedBy(loginUser);
        outletStaffRepository.update(request);
    }
}
