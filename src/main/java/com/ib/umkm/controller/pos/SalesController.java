package com.ib.umkm.controller.pos;

import com.ib.umkm.dto.pos.Sales;
import com.ib.umkm.dto.pos.SalesCreateRequest;
import com.ib.umkm.dto.pos.SalesReportDto;
import com.ib.umkm.security.JwtUser;
import com.ib.umkm.service.pos.SalesReportService;
import com.ib.umkm.service.pos.SalesService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
public class SalesController {

    private final SalesService salesService;
    private final SalesReportService salesReportService;

    public SalesController(SalesService salesService, SalesReportService salesReportService) {
        this.salesService = salesService;
        this.salesReportService = salesReportService;
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

    @GetMapping("/report")
    public List<SalesReportDto> report(@RequestParam(required = false) Long merchantId,
                                       @RequestParam LocalDate fromDate,
                                       @RequestParam LocalDate toDate) {
        if(fromDate == null){
            fromDate = LocalDate.now();
        }

        if(toDate == null ){
            toDate = LocalDate.now().plusDays(1);
        }
        return salesReportService.findSalesReport(merchantId,
                fromDate,
                toDate);
    }

}