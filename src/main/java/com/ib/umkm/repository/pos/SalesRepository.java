package com.ib.umkm.repository.pos;

import com.ib.umkm.dto.pos.Sales;
import com.ib.umkm.dto.pos.SalesItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class SalesRepository {
    private final JdbcTemplate jdbcTemplate;

    public SalesRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(Sales sales) {
        String sqlInsert = "INSERT INTO umkm.sales (" +
                "merchant_id, outlet_id, cashier_id, receipt_no, subtotal, " +
                "total_amount, payment_method, status, payment_status, created_by) " +
                "VALUES (?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?) ";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement ps =
                    connection.prepareStatement(sqlInsert, new String[]{"id"});

            ps.setLong(1, sales.getMerchantId());
            ps.setLong(2, sales.getOutletId());
            ps.setLong(3, sales.getCashierId());
            ps.setString(4, sales.getReceiptNo());
            ps.setBigDecimal(5, sales.getSubtotal());
            ps.setBigDecimal(6, sales.getTotalAmount());
            ps.setString(7, sales.getPaymentMethod());
            ps.setString(8, sales.getStatus());
            ps.setString(9, sales.getPaymentStatus());
            ps.setString(10, sales.getCreatedBy());

            return ps;

        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public int countToday(Long merchantId) {
        String sqlSelectCount = " SELECT COUNT(s) " +
                " FROM umkm.sales s " +
                " WHERE s.merchant_id =  " +  merchantId +
                " AND DATE(s.created_at) = CURRENT_DATE ";
        return jdbcTemplate.queryForObject(sqlSelectCount, Integer.class);
    }

    public Sales findById(Long id) {
        String sqlFindById = "select s.id, s.merchant_id, s.outlet_id, s.cashier_id, s.receipt_no, " +
                "s.total_amount, s.payment_method, s.status, s.subtotal, s.discount_amount, " +
                "s.tax_amount, s.payment_status, s.created_at, m.name merchant_name " +
                "FROM umkm.sales s " +
                "inner join umkm.merchant m on m.id = s.merchant_id " +
                "where s.id = ? ";
        Sales sales = jdbcTemplate.queryForObject(
                sqlFindById,
                salesRowMapper(),
                id
        );
        return sales;
    }

    public List<SalesItem> findItemsBySalesId(Long salesId) {

        String sql = "SELECT si.id, si.sales_id, si.product_id, si.product_name, si.qty, " +
                "si.price, si.subtotal " +
        " FROM umkm.sales_item si WHERE sales_id = ? ";

        return jdbcTemplate.query(
                sql,
                salesItemRowMapper(),
                salesId
        );
    }

    private RowMapper<Sales> salesRowMapper() {
        return (rs, rowNum) -> {
            Sales sales = new Sales();
            sales.setId(rs.getLong("id"));
            sales.setMerchantId(rs.getLong("merchant_id"));
            sales.setMerchantName(rs.getString("merchant_name"));
            sales.setOutletId(rs.getLong("outlet_id"));
            sales.setCashierId(rs.getLong("cashier_id"));
            sales.setReceiptNo(rs.getString("receipt_no"));
            sales.setTotalAmount(rs.getBigDecimal("total_amount"));
            sales.setPaymentMethod(rs.getString("payment_method"));
            sales.setStatus(rs.getString("status"));
            sales.setSubtotal(rs.getBigDecimal("subtotal"));
            sales.setDiscountAmount(rs.getBigDecimal("discount_amount"));
            sales.setTaxAmount(rs.getBigDecimal("tax_amount"));
            sales.setPaymentStatus(rs.getString("payment_status"));
            sales.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return sales;
        };
    }

    private RowMapper<SalesItem> salesItemRowMapper() {
        return (rs, rowNum) -> {
            SalesItem salesItem = new SalesItem();
            salesItem.setId(rs.getLong("id"));
            salesItem.setSalesId(rs.getLong("sales_id"));
            salesItem.setProductId(rs.getLong("product_id"));
            salesItem.setProductName(rs.getString("product_name"));
            salesItem.setQty(rs.getInt("qty"));
            salesItem.setPrice(rs.getBigDecimal("price"));
            salesItem.setSubtotal(rs.getBigDecimal("subtotal"));
            return salesItem;
        };
    }
}
