package com.ib.umkm.repository;

import com.ib.umkm.dto.ProductDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepository {
    private final JdbcTemplate jdbcTemplate;
    String sql = "select p.id, p.merchant_id, p.sku, p.name, p.cost_price, " +
            "p.selling_price, p.status, u.username owner_name, m.name merchant_name, p.category_id, " +
            "c.name category_name, p.created_by, p.created_at, p.updated_by, p.updated_at " +
            "from umkm.product p " +
            "inner join umkm.merchant m on p.merchant_id = m.id " +
            "inner join umkm.user_merchant um on um.merchant_id = m.id " +
            "inner join umkm.category c on p.category_id = c.id " +
            "inner join auth.users u on um.user_id = u.id "
            ;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ProductDto> findAll() {
        return jdbcTemplate.query(sql, productRowMapper());
    }

    private RowMapper<ProductDto> productRowMapper() {
        return (rs, rowNum) -> {
            ProductDto m = new ProductDto();
            m.setId(rs.getLong("id"));
            m.setName(rs.getString("name"));
            m.setSku(rs.getString("sku"));
            m.setCostPrice(rs.getBigDecimal("cost_price"));
            m.setSellingPrice(rs.getBigDecimal("selling_price"));
            m.setMerchantId(rs.getLong("merchant_id"));
            m.setMerchantName(rs.getString("merchant_name"));
            m.setOwnerName(rs.getString("owner_name"));
            m.setCategoryId(rs.getLong("category_id"));
            m.setCategoryName(rs.getString("category_name"));

            m.setCreatedBy(rs.getString("created_by"));
            m.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            if (rs.getTimestamp("updated_at") != null) {
                m.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }
            m.setUpdatedBy(rs.getString("updated_by"));
            return m;
        };
    }

    public ProductDto findById(Long id) {
        String sqlFindById = sql + " where p.id = ? ";
        ProductDto productDto = jdbcTemplate.queryForObject(
                sqlFindById,
                productRowMapper(),
                id
        );
        return productDto;
    }

    public List<ProductDto> findByOwnerId(Long userId) {
        String sqlFindByOwnerId = sql + " where um.user_id = ? ";
        List<ProductDto> products = jdbcTemplate.query(
                sqlFindByOwnerId,
                productRowMapper(),
                userId
        );
        return products;
    }

    public List<ProductDto> findByMerchantId(Long merchantId) {
        String sqlFindByOwnerId = sql + " where p.merchant_id = ? ";
        List<ProductDto> products = jdbcTemplate.query(
                sqlFindByOwnerId,
                productRowMapper(),
                merchantId
        );
        return products;
    }

    public void insert(ProductDto product) {
        String sqlInsert = "INSERT INTO umkm.product (merchant_id, sku, name, cost_price, selling_price, " +
                "category_id, status, created_by, updated_by) " +
                "VALUES (?, ?, ?, ?, ?, " +
                "?, ?, ?) ";

        jdbcTemplate.update(
            sqlInsert,
            product.getMerchantId(),
            product.getSku(),
            product.getName(),
            product.getCostPrice(),
            product.getSellingPrice(),
            product.getCategoryId(),
            product.getStatus(),
            product.getCreatedBy(),
            product.getCreatedBy()
        );
    }

    public void update(ProductDto product) {
        String sql = "update umkm.product " +
                "set name = ?, sku = ?, cost_price = ?, selling_price = ?, category_id = ?, " +
                "status = ?, updated_by = ?, updated_at = now() " +
                "where id = ? ";
        jdbcTemplate.update(
                sql,
                product.getName(),
                product.getSku(),
                product.getCostPrice(),
                product.getSellingPrice(),
                product.getCategoryId(),
                product.getStatus(),
                product.getUpdatedBy(),
                product.getId()
        );
    }
}
