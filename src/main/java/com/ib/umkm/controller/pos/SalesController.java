package com.ib.umkm.controller.pos;

import com.ib.umkm.common.ApiResponse;
import com.ib.umkm.common.PageResult;
import com.ib.umkm.dto.pos.*;
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

    @GetMapping("/reports_sales")
    public ResponseEntity<ApiResponse<PageResult<SalesReportSummaryDto>>> merchants(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String keyword) {

        JwtUser jwtUser = (JwtUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        PageResult<SalesReportSummaryDto> result;

        if (jwtUser.getRole().contains("ADMIN")) {
            result = salesReportService.findPagedSummary(page, size, keyword);
        } else {
            result = salesReportService.findPagedSummaryByUserId(page, size, jwtUser.getUserId(), keyword);
        }

        ApiResponse<PageResult<SalesReportSummaryDto>> response =
                new ApiResponse<>(
                        true,
                        "SUCCESS",
                        "Sales Report Summaries fetched successfully",
                        result
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports_sales_detail/{outletId}/{salesDate}")
    public ResponseEntity<ApiResponse<PageResult<SalesReportDto>>> reportSalesDetail(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String keyword,
        @PathVariable Long outletId,
        @PathVariable LocalDate salesDate) {

        PageResult<SalesReportDto> result;
        result = salesReportService.findPagedByOutletIdDate(page, size, keyword, outletId, salesDate);

        ApiResponse<PageResult<SalesReportDto>> response =
                new ApiResponse<>(
                        true,
                        "SUCCESS",
                        "Sales report details fetched successfully",
                        result
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports_sales_detail_item/{salesId}")
    public SalesReportDto reportSalesDetailItem(@PathVariable Long salesId) {
        SalesReportDto  salesReportDto = salesReportService.getSalesById(salesId);
        List<SalesItem> items = salesReportService.getItemsBySalesId(salesId);
        salesReportDto.setItems(items);
        return salesReportDto;
    }

}