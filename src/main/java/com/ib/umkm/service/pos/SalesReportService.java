package com.ib.umkm.service.pos;

import com.ib.umkm.common.PageResult;
import com.ib.umkm.dto.pos.SalesReportDto;
import com.ib.umkm.dto.pos.SalesReportSummaryDto;
import com.ib.umkm.repository.pos.SalesReportRepository;
import com.ib.umkm.repository.pos.SalesReportSummaryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SalesReportService {
    private final SalesReportSummaryRepository salesReportSummaryRepository;
    private final SalesReportRepository salesReportRepository;

    public SalesReportService(SalesReportSummaryRepository salesReportSummaryRepository,
                              SalesReportRepository salesReportRepository) {
        this.salesReportSummaryRepository = salesReportSummaryRepository;
        this.salesReportRepository = salesReportRepository;
    }

    public PageResult<SalesReportSummaryDto> findPagedSummary(int page, int size, String keyword) {
        int offset = page * size;
        List<SalesReportSummaryDto> salesReports = salesReportSummaryRepository.findAllGroupByDate(size, offset, keyword);
        int total = salesReportSummaryRepository.countAllByDate(keyword);
        return new PageResult<>(salesReports, page, size, total);
    }

    public PageResult<SalesReportSummaryDto> findPagedSummaryByUserId(int page, int size, Long userId, String keyword) {
        int offset = page * size;
        List<SalesReportSummaryDto> salesReports = salesReportSummaryRepository.findAllGroupByDateByUserId(size, offset, keyword, userId);
        int total = salesReportSummaryRepository.countAllByDateUserId(keyword, userId);
        return new PageResult<>(salesReports, page, size, total);
    }

    public PageResult<SalesReportDto> findPagedByOutletIdDate(int page, int size, String keyword, Long outletId, LocalDate salesDate) {
        int offset = page * size;
        List<SalesReportDto> salesReports = salesReportRepository.findAll(size, offset, keyword, outletId, salesDate);
        int total = salesReportRepository.countAll(keyword, outletId, salesDate);
        return new PageResult<>(salesReports, page, size, total);
    }

}
