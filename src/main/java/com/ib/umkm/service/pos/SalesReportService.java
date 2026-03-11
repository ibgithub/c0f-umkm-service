package com.ib.umkm.service.pos;

import com.ib.umkm.common.PageResult;
import com.ib.umkm.dto.pos.SalesReportDto;
import com.ib.umkm.dto.pos.SalesReportSummaryDto;
import com.ib.umkm.repository.pos.SalesReportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SalesReportService {
    private final SalesReportRepository salesReportRepository;

    public SalesReportService(SalesReportRepository salesReportRepository) {
        this.salesReportRepository = salesReportRepository;
    }

    public List<SalesReportDto> findSalesReport(Long merchantId, LocalDate startDate, LocalDate endDate) {
        return salesReportRepository.findSalesReport(merchantId, startDate, endDate);
    }

    public PageResult<SalesReportSummaryDto> findPagedSummary(int page, int size, String keyword) {
        int offset = page * size;
        List<SalesReportSummaryDto> salesReports = salesReportRepository.findAllGroupByDate(size, offset, keyword);
        int total = salesReportRepository.countAllByDate(keyword);

        return new PageResult<>(salesReports, page, size, total);
    }

    public PageResult<SalesReportSummaryDto> findPagedSummaryByUserId(int page, int size, Long userId, String keyword) {
        int offset = page * size;
        List<SalesReportSummaryDto> salesReports = salesReportRepository.findAllGroupByDateByUserId(size, offset, keyword, userId);
        int total = salesReportRepository.countAllByDateUserId(keyword, userId);

        return new PageResult<>(salesReports, page, size, total);
    }

    public PageResult<SalesReportDto> findPaged(int page, int size, String keyword, LocalDate fromDate, LocalDate toDate, Long merchantId) {

        int offset = page * size;
        List<SalesReportDto> salesReports = null;
        Integer total = null;
        if(merchantId==null){
            salesReports = salesReportRepository.findAll(size, offset, keyword, fromDate, toDate);
            total = salesReportRepository.countAll(keyword, fromDate, toDate);
        } else {
            salesReports = salesReportRepository.findAllByMerchantId(size, offset, keyword, fromDate, toDate, merchantId);
            total = salesReportRepository.countByMerchantId(keyword, fromDate, toDate, merchantId);
        }

        return new PageResult<>(salesReports, page, size, total);
    }

}
