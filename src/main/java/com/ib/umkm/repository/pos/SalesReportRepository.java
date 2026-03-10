package com.ib.umkm.repository.pos;

import com.ib.umkm.dto.pos.SalesReportDto;
import com.ib.umkm.dto.pos.SalesReportSummaryDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class SalesReportRepository {
    public static final String GROUP_BY_MERCHANT_NAME_S_OUTLET_ID_OUTLET_NAME_DATE_S_CREATED_AT = "group by merchant_name, s.outlet_id, outlet_name, date(s.created_at) ";
    private final JdbcTemplate jdbcTemplate;

    private String sql = "SELECT s.id, s.receipt_no, s.created_at, s.total_amount, s.payment_method " +
            "FROM umkm.sales s " +
            "WHERE s.created_at >= ? AND s.created_at < ? ";
    private String sqlCount = "SELECT count(1) " +
            "FROM umkm.sales s ";

    private String orderby = "ORDER BY s.created_at DESC ";

    private String sqlGroupByPerDate = "select m.name merchant_name, s.outlet_id, o.name outlet_name, date(s.created_at) sales_date, sum(s.total_amount) total_amount " +
            "from umkm.sales s " +
            "inner join umkm.merchant m on m.id = s.merchant_id " +
            "inner join umkm.outlet o on o.id = s.outlet_id ";
    private String groupbyOrderbyPerDate =
            GROUP_BY_MERCHANT_NAME_S_OUTLET_ID_OUTLET_NAME_DATE_S_CREATED_AT +
            "order by merchant_name, s.outlet_id, outlet_name, date(s.created_at) desc";

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

    public List<SalesReportSummaryDto> findAllGroupByPerDate(int limit, int offset, String keyword
                                        ) {
        String sqlSelect = sqlGroupByPerDate;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlSelect += " where ( upper(m.name) like CONCAT('%', ?, '%') or upper(o.name) like CONCAT('%', ?, '%') ) " +
                    groupbyOrderbyPerDate + " LIMIT ? OFFSET ? ";
            return jdbcTemplate.query(sqlSelect,
                    salesReportSummaryRowMapper(),
                    keyword, keyword,
                    limit, offset);
        }
        sqlSelect += groupbyOrderbyPerDate + " LIMIT ? OFFSET ? ";

        return jdbcTemplate.query(sqlSelect,
                salesReportSummaryRowMapper(),
                limit, offset);
    }

    public int countAllPerDate(String keyword) {
        String sqlSelectCount = "SELECT COUNT(*) FROM ( " +
                "select m.name merchant_name, s.outlet_id, o.name outlet_name, date(s.created_at) sales_date, sum(s.total_amount) total_amount " +
                "from umkm.sales s " +
                "inner join umkm.merchant m on m.id = s.merchant_id " +
                "inner join umkm.outlet o on o.id = s.outlet_id ";
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlSelectCount += " where ( upper(m.name) like CONCAT('%', " + keyword + ", '%') or upper(o.name) like CONCAT('%', ?, '%') ) " +
                    GROUP_BY_MERCHANT_NAME_S_OUTLET_ID_OUTLET_NAME_DATE_S_CREATED_AT + " ) a ";
            return jdbcTemplate.queryForObject(sqlSelectCount, Integer.class);
        }
        sqlSelectCount += GROUP_BY_MERCHANT_NAME_S_OUTLET_ID_OUTLET_NAME_DATE_S_CREATED_AT + " ) a ";
        return jdbcTemplate.queryForObject(sqlSelectCount, Integer.class);
    }

    public List<SalesReportDto> findAll(int limit, int offset, String keyword,
                                        LocalDate fromDate,
                                        LocalDate toDate) {
        String sqlSelect = sql;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlSelect += " and upper(s.receipt_no) like CONCAT('%', ?, '%') " +
                    orderby +
                    " LIMIT ? OFFSET ? ";
            return jdbcTemplate.query(sqlSelect,
                    salesReportRowMapper(),
                    fromDate.atStartOfDay(),
                    toDate.atStartOfDay(),
                    keyword,
                    limit, offset);
        }
        sqlSelect += orderby + " LIMIT ? OFFSET ? " ;

        return jdbcTemplate.query(sqlSelect,
                salesReportRowMapper(),
                fromDate.atStartOfDay(),
                toDate.atStartOfDay(),
                limit, offset);
    }

    public int countAll(String keyword, LocalDate fromDate, LocalDate toDate) {
        String sqlSelectCount = sqlCount + " WHERE s.created_at >= " + fromDate + " AND s.created_at < " + toDate + " ";
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlSelectCount += " and upper(s.receipt_no) like CONCAT('%', " + keyword +", '%') ";
            return jdbcTemplate.queryForObject(sqlSelectCount, Integer.class);
        }
        return jdbcTemplate.queryForObject(sqlSelectCount, Integer.class);
    }

    public List<SalesReportDto> findAllByMerchantId(int limit, int offset, String keyword,
                                        LocalDate fromDate,
                                        LocalDate toDate,
                                        Long merchantId) {
        String sqlSelect = sql;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlSelect += " and upper(s.receipt_no) like CONCAT('%', ?, '%') " +
                    " and s.merchant_id = ? " +
                    orderby +
                    " LIMIT ? OFFSET ? ";
            return jdbcTemplate.query(sqlSelect,
                    salesReportRowMapper(),
                    fromDate.atStartOfDay(),
                    toDate.atStartOfDay(),
                    keyword,
                    merchantId,
                    limit, offset);
        }
        sqlSelect +=  " and s.merchant_id = ? " + orderby + " LIMIT ? OFFSET ? " ;

        return jdbcTemplate.query(sqlSelect,
                salesReportRowMapper(),
                fromDate.atStartOfDay(),
                toDate.atStartOfDay(),
                merchantId,
                limit, offset);
    }

    public int countByMerchantId(String keyword, LocalDate fromDate, LocalDate toDate, Long merchantId) {
        String sqlSelectCount = sqlCount + " WHERE s.created_at >= " + fromDate + " AND s.created_at < " + toDate + " and s.merchant_id = "+  merchantId +" ";
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
            salesReport.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return salesReport;
        };
    }
    private RowMapper<SalesReportSummaryDto> salesReportSummaryRowMapper() {
        return (rs, rowNum) -> {
            SalesReportSummaryDto salesSummary = new SalesReportSummaryDto();
            salesSummary.setMerchantName(rs.getString("merchant_name"));
            salesSummary.setOutletId(rs.getLong("outlet_id"));
            salesSummary.setOutletName(rs.getString("outlet_name"));
            salesSummary.setSalesDate(rs.getObject("sales_date", LocalDate.class) );
            salesSummary.setTotalAmount(rs.getBigDecimal("total_amount"));
            return salesSummary;
        };
    }
}
