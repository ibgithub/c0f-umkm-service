package com.ib.umkm.controller;

import com.ib.umkm.common.ApiResponse;
import com.ib.umkm.common.PageResult;
import com.ib.umkm.dto.MerchantDto;
import com.ib.umkm.security.JwtUser;
import com.ib.umkm.service.MerchantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @GetMapping("/data")
    public List<MerchantDto> merchants(Authentication authentication) {
        String username = authentication.getName();
        System.out.println("Request by: " + username);

        JwtUser jwtUser = (JwtUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (jwtUser.getRole().contains("ADMIN")) {
            return merchantService.getAllMerchants();
        }

        return merchantService.getMerchantsByOwnerId(jwtUser.getUserId());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResult<MerchantDto>>> merchants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        JwtUser jwtUser = (JwtUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        PageResult<MerchantDto> result;

        if (jwtUser.getRole().contains("ADMIN")) {
            result = merchantService.findPaged(page, size);
        } else {
            result = merchantService.findPagedByOwnerId(page, size, jwtUser.getUserId());
        }

        ApiResponse<PageResult<MerchantDto>> response =
                new ApiResponse<>(
                        true,
                        "SUCCESS",
                        "Merchants fetched successfully",
                        result
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public void createMerchant(@RequestBody MerchantDto request) {

        JwtUser jwtUser = (JwtUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        merchantService.createMerchant(request, jwtUser.getUsername());
    }

    @GetMapping("/{id}")
    public MerchantDto getById(@PathVariable Long id) {
        return merchantService.getById(id);
    }

    @PutMapping("/{id}")
    public void updateUser(
            @PathVariable Long id,
            @RequestBody MerchantDto request
    ) {
        String username = (String) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

        request.setId(id);
        merchantService.updateMerchant(request, username);
    }

}
