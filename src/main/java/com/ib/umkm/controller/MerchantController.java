package com.ib.umkm.controller;

import com.ib.umkm.dto.MerchantDto;
import com.ib.umkm.service.MerchantService;
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

    @GetMapping
    public List<MerchantDto> merchants(Authentication authentication) {
        String username = authentication.getName();
        System.out.println("Request by: " + username);

        return merchantService.getAllMerchants();
    }

    @PostMapping
    public void createUser(@RequestBody MerchantDto request) {

        String username = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        merchantService.createMerchant(request, username);
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
