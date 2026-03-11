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

    private String sql = "SELECT s.id, s.receipt_no, s.created_at, s.total_amount, s.payment_method, " +
            "s.payment_status, s.status " +
            "FROM umkm.sales s " +
            "WHERE s.outlet_id = ? AND date(s.created_at) = ? ";

    private String sqlCount = "SELECT count(1) " +
            "FROM umkm.sales s " ;

    private String orderby = "ORDER BY s.created_at DESC ";

    public SalesReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SalesReportDto> findAll(int limit, int offset, String keyword, Long outletId,
                                        LocalDate salesDate) {
        String sqlSelect = sql;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlSelect += " and upper(s.receipt_no) like CONCAT('%', ?, '%') " +
                    orderby +
                    " LIMIT ? OFFSET ? ";
            return jdbcTemplate.query(sqlSelect,
                    salesReportRowMapper(),
                    outletId, salesDate,
                    keyword,
                    limit, offset);
        }
        sqlSelect += orderby + " LIMIT ? OFFSET ? " ;

        return jdbcTemplate.query(sqlSelect,
                salesReportRowMapper(),
                outletId, salesDate,
                limit, offset);
    }

    public int countAll(String keyword, Long outletId, LocalDate salesDate) {
        String sqlSelectCount = sqlCount + " WHERE s.outlet_id = '" + outletId + "' AND date(s.created_at) = '" + salesDate + "' ";
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlSelectCount += " and upper(s.receipt_no) like CONCAT('%', " + keyword +", '%') ";
            return jdbcTemplate.queryForObject(sqlSelectCount, Integer.class);
        }
        return jdbcTemplate.queryForObject(sqlSelectCount, Integer.class);
    }

    private RowMapper<SalesReportDto> salesReportRowMapper() {
        return (rs, rowNum) -> {
            SalesReportDto salesReport = new SalesReportDto();
            salesReport.setId(rs.getLong("id"));
            salesReport.setReceiptNo(rs.getString("receipt_no"));
            salesReport.setTotalAmount(rs.getBigDecimal("total_amount"));
            salesReport.setPaymentMethod(rs.getString("payment_method"));
            salesReport.setPaymentStatus(rs.getString("payment_status"));
            salesReport.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            salesReport.setStatus(rs.getString("status"));
            return salesReport;
        };
    }

}
