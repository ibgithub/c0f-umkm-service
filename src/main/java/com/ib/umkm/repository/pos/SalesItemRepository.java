package com.ib.umkm.repository.pos;

import com.ib.umkm.dto.pos.SalesItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SalesItemRepository {
    private final JdbcTemplate jdbcTemplate;
    public SalesItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public void save(SalesItem salesItem) {
        String sqlInsert = "INSERT INTO umkm.sales_item (" +
                "sales_id, product_id, product_name, qty, price, " +
                "subtotal, created_by) " +
                "VALUES (?, ?, ?, ?, ?, " +
                "?, ?) ";

        jdbcTemplate.update(
                sqlInsert,
                salesItem.getSalesId(),
                salesItem.getProductId(),
                salesItem.getProductName(),
                salesItem.getQty(),
                salesItem.getPrice(),
                salesItem.getSubtotal(),
                salesItem.getCreatedBy()
        );
    }
}
