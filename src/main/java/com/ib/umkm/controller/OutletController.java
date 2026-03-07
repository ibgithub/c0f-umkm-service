package com.ib.umkm.controller;

import com.ib.umkm.common.ApiResponse;
import com.ib.umkm.common.PageResult;
import com.ib.umkm.dto.OutletDto;
import com.ib.umkm.security.JwtUser;
import com.ib.umkm.service.OutletService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/outlets")
public class OutletController {

    private final OutletService outletService;

    public OutletController(OutletService outletService) {
        this.outletService = outletService;
    }

    @GetMapping("/data")
    public List<OutletDto> outlets(Authentication authentication) {
        JwtUser jwtUser = (JwtUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (jwtUser.getRole().contains("ADMIN")) {
            return outletService.getOutlets();
        }
        return outletService.getOutletsByOwnerId(jwtUser.getUserId());
    }

    @GetMapping("/byMerchant/{merchantId}")
    public List<OutletDto> outletsByMerchant(@PathVariable Long merchantId) {
        return outletService.getOutletsByMerchantId(merchantId);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResult<OutletDto>>> outlets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        JwtUser jwtUser = (JwtUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        PageResult<OutletDto> result;

        if (jwtUser.getRole().contains("ADMIN")) {
            result = outletService.findPaged(page, size, keyword);
        } else {
            result = outletService.findPagedByOwnerId(page, size, jwtUser.getUserId(), keyword);
        }

        ApiResponse<PageResult<OutletDto>> response =
                new ApiResponse<>(
                        true,
                        "SUCCESS",
                        "Outlets fetched successfully",
                        result
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public void createOutlet(@RequestBody OutletDto request) {

        JwtUser jwtUser = (JwtUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        outletService.createOutlet(request, jwtUser.getUsername());
    }

    @GetMapping("/{id}")
    public OutletDto getById(@PathVariable Long id) {
        return outletService.getById(id);
    }

    @PutMapping("/{id}")
    public void updateOutlet(
            @PathVariable Long id,
            @RequestBody OutletDto outlet
    ) {
        JwtUser jwtUser = (JwtUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        outlet.setId(id);
        outletService.updateOutlet(outlet, jwtUser.getUsername());
    }
}
