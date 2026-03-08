package com.ib.umkm.repository;

import com.ib.umkm.dto.ProductDto;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepository {
    private final JdbcTemplate jdbcTemplate;
    String sql = "select p.id, p.merchant_id, p.name, p.cost_price, " +
            "p.selling_price, p.status, u.username owner_name, m.name merchant_name, p.category_id, " +
            "c.name category_name, p.created_by, p.created_at, p.updated_by, p.updated_at " +
            "from umkm.product p " +
            "inner join umkm.merchant m on p.merchant_id = m.id " +
            "inner join umkm.user_merchant um on um.merchant_id = m.id " +
            "inner join umkm.category c on p.category_id = c.id " +
            "inner join auth.users u on um.user_id = u.id "
            ;
    String sqlSimple = "select p.id, p.merchant_id, p.name, p.cost_price, " +
            "p.selling_price, p.status, p.category_id " +
            "from umkm.product p "
            ;
    String sqlCount = "SELECT COUNT(1) " +
            "from umkm.product p " +
            "inner join umkm.merchant m on p.merchant_id = m.id " +
            "inner join umkm.user_merchant um on um.merchant_id = m.id " +
            "inner join umkm.category c on p.category_id = c.id " +
            "inner join auth.users u on um.user_id = u.id "
            ;
    String order_by = " order by m.name, c.name, p.name ";

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ProductDto> findAll() {
        return jdbcTemplate.query(sql, productRowMapper());
    }

    public List<ProductDto> findAll(int limit, int offset, String keyword) {
        String sqlSelect = sql;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlSelect += " where ( upper(m.name) like CONCAT('%', ?, '%') or upper(c.name) like CONCAT('%', ?, '%') or upper(p.name) like CONCAT('%', ?, '%') ) " +
                    order_by +
                    " LIMIT ? OFFSET ? ";
            return jdbcTemplate.query(sqlSelect,
                    new BeanPropertyRowMapper<>(ProductDto.class),
                    keyword, keyword, keyword,
                    limit, offset);
        }
        sqlSelect += order_by + " LIMIT ? OFFSET ? ";

        return jdbcTemplate.query(sqlSelect,
                new BeanPropertyRowMapper<>(ProductDto.class),
                limit, offset);
    }

    public List<ProductDto> findByOwnerId(int limit, int offset, Long userId, String keyword) {
        String sqlSelect = sql;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlSelect += " where ( upper(m.name) like CONCAT('%', ?, '%') or upper(c.name) like CONCAT('%', ?, '%') or upper(p.name) like CONCAT('%', ?, '%') ) " +
                    " and um.user_id = ? " +
                    order_by +
                    " LIMIT ? OFFSET ? ";
            return jdbcTemplate.query(sqlSelect,
                    new BeanPropertyRowMapper<>(ProductDto.class),
                    keyword, keyword, keyword,
                    userId,
                    limit, offset);
        }
        String sqlFindByOwnerId = sql + " where um.user_id = ? " + order_by + " LIMIT ? OFFSET ? " ;
        return jdbcTemplate.query(sqlFindByOwnerId,
                new BeanPropertyRowMapper<>(ProductDto.class),
                userId,
                limit, offset);
    }

    public int countAll(String keyword) {
        String sqlSelectCount = sqlCount;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlSelectCount += " where ( upper(m.name) like CONCAT('%" + keyword + "%') or upper(c.name) like CONCAT('%" + keyword + "%') or upper(p.name) like CONCAT('%" + keyword + "%') ) ";
            return jdbcTemplate.queryForObject(sqlSelectCount, Integer.class);
        }
        return jdbcTemplate.queryForObject(sqlSelectCount, Integer.class);
    }

    public int countAllByOwnerId(long ownerId, String keyword) {
        String sqlCountByOwnerId = sqlCount + " where um.user_id = " + ownerId;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlCountByOwnerId += " and ( upper(m.name) like CONCAT('%" + keyword + "%') or upper(c.name) like CONCAT('%" + keyword + "%') or upper(p.name) like CONCAT('%" + keyword + "%') ) ";
        }
        return jdbcTemplate.queryForObject(sqlCountByOwnerId, Integer.class);
    }

    private RowMapper<ProductDto> productRowMapper() {
        return (rs, rowNum) -> {
            ProductDto m = new ProductDto();
            m.setId(rs.getLong("id"));
            m.setName(rs.getString("name"));
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

    private RowMapper<ProductDto> productSimpleRowMapper() {
        return (rs, rowNum) -> {
            ProductDto m = new ProductDto();
            m.setId(rs.getLong("id"));
            m.setName(rs.getString("name"));
            m.setCostPrice(rs.getBigDecimal("cost_price"));
            m.setSellingPrice(rs.getBigDecimal("selling_price"));
            m.setMerchantId(rs.getLong("merchant_id"));
            m.setCategoryId(rs.getLong("category_id"));
            return m;
        };
    }

    public ProductDto findById(Long id) {
        String sqlFindById = sqlSimple + " where p.id = ? ";
        ProductDto productDto = jdbcTemplate.queryForObject(
                sqlFindById,
                productSimpleRowMapper(),
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
        String sqlFindByOwnerId = sqlSimple + " where p.merchant_id = ? ";
        List<ProductDto> products = jdbcTemplate.query(
                sqlFindByOwnerId,
                productSimpleRowMapper(),
                merchantId
        );
        return products;
    }

    public void insert(ProductDto product) {
        String sqlInsert = "INSERT INTO umkm.product (merchant_id, name, cost_price, selling_price, category_id, " +
                "status, created_by, updated_by) " +
                "VALUES (?, ?, ?, ?, ?, " +
                "?, ?, ?) ";

        jdbcTemplate.update(
            sqlInsert,
            product.getMerchantId(),
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
                "set name = ?, cost_price = ?, selling_price = ?, category_id = ?, status = ?, " +
                "updated_by = ?, updated_at = now() " +
                "where id = ? ";
        jdbcTemplate.update(
                sql,
                product.getName(),
                product.getCostPrice(),
                product.getSellingPrice(),
                product.getCategoryId(),
                product.getStatus(),
                product.getUpdatedBy(),
                product.getId()
        );
    }
}
