package com.ib.umkm.controller;

import com.ib.umkm.dto.MerchantDto;
import com.ib.umkm.service.MerchantService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
