package com.ib.umkm.controller.pos;

import com.ib.umkm.dto.pos.SalesCreateRequest;
import com.ib.umkm.security.JwtUser;
import com.ib.umkm.service.pos.SalesService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sales")
public class SalesController {

    private final SalesService salesService;

    public SalesController(SalesService salesService) {
        this.salesService = salesService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody SalesCreateRequest req) {

        JwtUser user = (JwtUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        salesService.createSales(req, user.getUserId(), user.getUsername());

        return ResponseEntity.ok().build();
    }

}