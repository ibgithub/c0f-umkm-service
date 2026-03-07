package com.ib.umkm.controller;

import com.ib.umkm.common.ApiResponse;
import com.ib.umkm.common.PageResult;
import com.ib.umkm.dto.OutletStaffDto;
import com.ib.umkm.security.JwtUser;
import com.ib.umkm.service.OutletStaffService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/outlet-staffs")
public class OutletStaffController {

    private final OutletStaffService outletStaffService;

    public OutletStaffController(OutletStaffService outletStaffService) {
        this.outletStaffService = outletStaffService;
    }

    @GetMapping("/data")
    public List<OutletStaffDto> outlets(Authentication authentication) {
        JwtUser jwtUser = (JwtUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (jwtUser.getRole().contains("ADMIN")) {
            return outletStaffService.getOutletStaffs();
        }
        return outletStaffService.getOutletStaffsByOwnerId(jwtUser.getUserId());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResult<OutletStaffDto>>> outlets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        JwtUser jwtUser = (JwtUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        PageResult<OutletStaffDto> result;

        if (jwtUser.getRole().contains("ADMIN")) {
            result = outletStaffService.findPaged(page, size, keyword);
        } else {
            result = outletStaffService.findPagedByOwnerId(page, size, jwtUser.getUserId(), keyword);
        }

        ApiResponse<PageResult<OutletStaffDto>> response =
                new ApiResponse<>(
                        true,
                        "SUCCESS",
                        "Outlet Staffs fetched successfully",
                        result
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public void createOutlet(@RequestBody OutletStaffDto request) {

        JwtUser jwtUser = (JwtUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        outletStaffService.createOutletStaff(request, jwtUser.getUsername());
    }

    @GetMapping("/{id}")
    public OutletStaffDto getById(@PathVariable Long id) {
        return outletStaffService.getById(id);
    }

    @PutMapping("/{id}")
    public void updateOutletStaff(
            @PathVariable Long id,
            @RequestBody OutletStaffDto outletStaff
    ) {
        JwtUser jwtUser = (JwtUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        outletStaff.setId(id);
        outletStaffService.updateOutletStaff(outletStaff, jwtUser.getUsername());
    }
}
