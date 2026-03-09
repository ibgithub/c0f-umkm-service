package com.ib.umkm.service.pos;

import com.ib.umkm.dto.pos.SalesReportDto;
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

}
