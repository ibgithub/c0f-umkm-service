package com.ib.umkm.controller.pos;

import com.ib.umkm.dto.pos.Sales;
import com.ib.umkm.dto.pos.SalesCreateRequest;
import com.ib.umkm.security.JwtUser;
import com.ib.umkm.service.pos.SalesService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

        Long salesId = salesService.createSales(
                req,
                user.getUserId(),
                user.getUsername()
        );

        return ResponseEntity.ok(
                Map.of("id", salesId)
        );
    }

    @GetMapping("/{id}")
    public Sales getById(@PathVariable Long id) {
        return salesService.getById(id);
    }

}