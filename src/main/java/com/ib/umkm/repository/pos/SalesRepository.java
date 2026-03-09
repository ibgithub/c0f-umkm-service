package com.ib.umkm.repository.pos;

import com.ib.umkm.dto.pos.Sales;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;

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


}
