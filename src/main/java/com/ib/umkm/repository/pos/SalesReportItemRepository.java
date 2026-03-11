package com.ib.umkm.repository.pos;

import com.ib.umkm.dto.pos.SalesItem;
import com.ib.umkm.dto.pos.SalesReportDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class SalesReportItemRepository {
    private final JdbcTemplate jdbcTemplate;

    private String sql = "SELECT i.sales_id, i.product_name, i.qty, i.price, i.subtotal, " +
            "i.discount " +
            "FROM umkm.sales_item i " +
            "WHERE i.sales_id = ? ORDER BY i.id ";

    private String sqlSales = "select s.receipt_no, date(s.created_at) sales_date, o.name outlet_name, u.first_name, u.last_name, " +
            "s.payment_method, s.status, s.subtotal, s.discount_amount, s.tax_amount, " +
            "s.total_amount, s.created_at " +
            "from umkm.sales s " +
            "inner join umkm.outlet o on o.id = s.outlet_id " +
            "inner join auth.users u on u.id = s.cashier_id " +
            "where s.id = ? ";

    public SalesReportItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SalesItem> getItemBySalesId(Long salesId) {
        return jdbcTemplate.query(sql,
                salesItemRowMapper(),
                salesId);
    }

    public SalesReportDto getSalesById(Long id) {
        SalesReportDto salesReportDto = jdbcTemplate.queryForObject(
                sqlSales,
                salesRowMapper(),
                id
        );
        return salesReportDto;
    }

    private RowMapper<SalesItem> salesItemRowMapper() {
        return (rs, rowNum) -> {
            SalesItem salesItem = new SalesItem();
            salesItem.setProductName(rs.getString("product_name"));
            salesItem.setQty(rs.getInt("qty"));
            salesItem.setPrice(rs.getBigDecimal("price"));
            salesItem.setSubtotal(rs.getBigDecimal("subtotal"));
            salesItem.setDiscount(rs.getBigDecimal("discount"));
            return salesItem;
        };
    }

    private RowMapper<SalesReportDto> salesRowMapper() {
        return (rs, rowNum) -> {
            SalesReportDto sales = new SalesReportDto();
            sales.setReceiptNo(rs.getString("receipt_no"));
            sales.setSalesDate(rs.getObject("sales_date", LocalDate.class));
            sales.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            sales.setOutletName(rs.getString("outlet_name"));
            String firstName =  rs.getString("first_name");
            String lastName = rs.getString("last_name");
            firstName = (firstName == null ? "" : firstName);
            lastName = (lastName == null ? "" : lastName);
            String fullName = firstName + (lastName.isEmpty() ? "" : " " + lastName);
            sales.setCashierName(fullName);
            sales.setPaymentMethod(rs.getString("payment_method"));
            sales.setStatus(rs.getString("status"));
            sales.setSubtotal(rs.getBigDecimal("subtotal"));
            sales.setDiscountAmount(rs.getBigDecimal("discount_amount"));
            sales.setTaxAmount(rs.getBigDecimal("tax_amount"));
            sales.setTotalAmount(rs.getBigDecimal("total_amount"));
            return sales;
        };
    }

}
