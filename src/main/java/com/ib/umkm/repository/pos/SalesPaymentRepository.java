package com.ib.umkm.repository.pos;

import com.ib.umkm.dto.pos.SalesPayment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SalesPaymentRepository {
    private final JdbcTemplate jdbcTemplate;
    public SalesPaymentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public void save(SalesPayment salesPayment) {
        String sqlInsert = "INSERT INTO umkm.sales_payment (" +
                "sales_id, payment_method, amount) VALUES (?, ?, ?) ";
        jdbcTemplate.update(
                sqlInsert,
                salesPayment.getSalesId(),
                salesPayment.getPaymentMethod(),
                salesPayment.getAmount()
        );
    }
}
