package com.ib.umkm.repository.pos;

import com.ib.umkm.dto.pos.SalesReportDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class SalesReportRepository {
    private final JdbcTemplate jdbcTemplate;

    private String sql = "SELECT s.id, s.receipt_no, s.created_at, s.total_amount, s.payment_method " +
            "FROM umkm.sales s " +
            "WHERE s.created_at >= ? AND s.created_at < ? ";
    private String orderby = "ORDER BY s.created_at DESC ";

    public SalesReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public List<SalesReportDto> findSalesReport(
            Long merchantId,
            LocalDate fromDate,
            LocalDate toDate
    ){
        if (merchantId == null) {
            String sqlSelect = sql + orderby;
            return jdbcTemplate.query(
                    sqlSelect,
                    salesReportRowMapper(),
                    fromDate.atStartOfDay(),
                    toDate.atStartOfDay()
            );
        } else {
            String sqlSelect = sql + " and s.merchant_id = ? " + orderby;
            return jdbcTemplate.query(
                    sqlSelect,
                    salesReportRowMapper(),
                    fromDate.atStartOfDay(),
                    toDate.atStartOfDay(),
                    merchantId
            );
        }

    }

    private RowMapper<SalesReportDto> salesReportRowMapper() {
        return (rs, rowNum) -> {
            SalesReportDto salesReport = new SalesReportDto();
            salesReport.setId(rs.getLong("id"));
            salesReport.setReceiptNo(rs.getString("receipt_no"));
            salesReport.setTotalAmount(rs.getBigDecimal("total_amount"));
            salesReport.setPaymentMethod(rs.getString("payment_method"));
            salesReport.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return salesReport;
        };
    }
}
