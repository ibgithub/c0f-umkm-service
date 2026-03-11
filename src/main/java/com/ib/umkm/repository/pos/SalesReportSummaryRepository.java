package com.ib.umkm.repository.pos;

import com.ib.umkm.dto.pos.SalesReportSummaryDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class SalesReportSummaryRepository {
    public static final String GROUP_BY_MERCHANT_NAME_S_OUTLET_ID_OUTLET_NAME_DATE_S_CREATED_AT = "group by merchant_name, s.outlet_id, outlet_name, date(s.created_at) ";
    private final JdbcTemplate jdbcTemplate;

    private String sqlGroupByDate = "select m.name merchant_name, s.outlet_id, o.name outlet_name, date(s.created_at) sales_date, sum(s.total_amount) total_amount " +
            "from umkm.sales s " +
            "inner join umkm.merchant m on m.id = s.merchant_id " +
            "inner join umkm.outlet o on o.id = s.outlet_id " +
            "inner join umkm.user_merchant um on um.merchant_id = m.id " +
            "inner join auth.users u on um.user_id = u.id ";
    private String groupbyOrderbyPerDate =
            GROUP_BY_MERCHANT_NAME_S_OUTLET_ID_OUTLET_NAME_DATE_S_CREATED_AT +
            "order by merchant_name, s.outlet_id, outlet_name, date(s.created_at) desc";

    public SalesReportSummaryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SalesReportSummaryDto> findAllGroupByDate(int limit, int offset, String keyword
                                        ) {
        String sqlSelect = sqlGroupByDate;
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

    public int countAllByDate(String keyword) {
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

    public List<SalesReportSummaryDto> findAllGroupByDateByUserId(int limit, int offset, String keyword, Long  userId
    ) {
        String sqlSelect = sqlGroupByDate + " where u.id = ? " ;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlSelect += " and ( upper(m.name) like CONCAT('%', ?, '%') or upper(o.name) like CONCAT('%', ?, '%') ) " +
                    groupbyOrderbyPerDate + " LIMIT ? OFFSET ? ";
            return jdbcTemplate.query(sqlSelect,
                    salesReportSummaryRowMapper(),
                    keyword, keyword,
                    userId,
                    limit, offset);
        }
        sqlSelect += groupbyOrderbyPerDate + " LIMIT ? OFFSET ? ";

        return jdbcTemplate.query(sqlSelect,
                salesReportSummaryRowMapper(),
                userId,
                limit, offset);
    }

    public int countAllByDateUserId(String keyword, Long userId) {
        String sqlSelectCount = "SELECT COUNT(*) FROM ( " +
                "select m.name merchant_name, s.outlet_id, o.name outlet_name, date(s.created_at) sales_date, sum(s.total_amount) total_amount " +
                "from umkm.sales s " +
                "inner join umkm.merchant m on m.id = s.merchant_id " +
                "inner join umkm.outlet o on o.id = s.outlet_id " +
                "inner join umkm.user_merchant um on um.merchant_id = m.id " +
                "inner join auth.users u on um.user_id = u.id " +
                "where u.id = " + userId + " ";
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlSelectCount += " and ( upper(m.name) like CONCAT('%', " + keyword + ", '%') or upper(o.name) like CONCAT('%', ?, '%') ) " +
                    GROUP_BY_MERCHANT_NAME_S_OUTLET_ID_OUTLET_NAME_DATE_S_CREATED_AT + " ) a ";
            return jdbcTemplate.queryForObject(sqlSelectCount, Integer.class);
        }
        sqlSelectCount += GROUP_BY_MERCHANT_NAME_S_OUTLET_ID_OUTLET_NAME_DATE_S_CREATED_AT + " ) a ";
        return jdbcTemplate.queryForObject(sqlSelectCount, Integer.class);
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
