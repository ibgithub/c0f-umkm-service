package com.ib.umkm.repository;

import com.ib.umkm.dto.CategoryDto;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryRepository {
    private final JdbcTemplate jdbcTemplate;
    private final SqlDataSourceScriptDatabaseInitializer sqlDataSourceScriptDatabaseInitializer;
    String sql = "select c.id, c.merchant_id, c.name, c.description, c.status, " +
            "m.name merchant_name, u.username owner_name, um.user_id owner_id " +
            "from umkm.category c " +
            "inner join umkm.merchant m on c.merchant_id = m.id " +
            "inner join umkm.user_merchant um on um.merchant_id = m.id " +
            "inner join auth.users u on um.user_id = u.id"
            ;
    String sqlCount = "SELECT COUNT(1) " +
            "from umkm.category c " +
            "inner join umkm.merchant m on c.merchant_id = m.id " +
            "inner join umkm.user_merchant um on um.merchant_id = m.id " +
            "inner join auth.users u on um.user_id = u.id"
            ;

    public CategoryRepository(JdbcTemplate jdbcTemplate, SqlDataSourceScriptDatabaseInitializer sqlDataSourceScriptDatabaseInitializer) {
        this.jdbcTemplate = jdbcTemplate;
        this.sqlDataSourceScriptDatabaseInitializer = sqlDataSourceScriptDatabaseInitializer;
    }

    public List<CategoryDto> findAll() {
        return jdbcTemplate.query(sql, categoryRowMapper());
    }

    public List<CategoryDto> findAll(int limit, int offset, String keyword) {
        String sqlSelect = sql;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlSelect += " where upper(u.username) like CONCAT('%', ?, '%') or upper(m.name) like CONCAT('%', ?, '%') or upper(c.name) like CONCAT('%', ?, '%') " +
                    " LIMIT ? OFFSET ? ";
            return jdbcTemplate.query(sqlSelect,
                    new BeanPropertyRowMapper<>(CategoryDto.class),
                    keyword, keyword,
                    limit, offset);
        }
        sqlSelect += " LIMIT ? OFFSET ? ";

        return jdbcTemplate.query(sqlSelect,
                new BeanPropertyRowMapper<>(CategoryDto.class),
                limit, offset);
    }

    public List<CategoryDto> findByOwnerId(int limit, int offset, Long userId, String keyword) {
        String sqlSelect = sql;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlSelect += " where upper(u.username) like CONCAT('%', ?, '%') or upper(m.name) like CONCAT('%', ?, '%') or upper(c.name) like CONCAT('%', ?, '%') " +
                    " LIMIT ? OFFSET ? where um.user_id = ? ";
            return jdbcTemplate.query(sqlSelect,
                    new BeanPropertyRowMapper<>(CategoryDto.class),
                    keyword, keyword,
                    limit, offset, userId);
        }
        String sqlFindByOwnerId = sql + " LIMIT ? OFFSET ? where um.user_id = ? ";
        return jdbcTemplate.query(sqlFindByOwnerId,
                new BeanPropertyRowMapper<>(CategoryDto.class),
                keyword, keyword,
                limit, offset, userId);
    }

    public int countAll(String keyword) {
        String sqlSelectCount = sqlCount;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlSelectCount += " where upper(u.username) like CONCAT('%" + keyword + "%') or upper(m.name) like CONCAT('%" + keyword + "%') or upper(c.name) like CONCAT('%" + keyword + "%') ";
            return jdbcTemplate.queryForObject(sqlSelectCount, Integer.class);
        }
        return jdbcTemplate.queryForObject(sqlSelectCount, Integer.class);
    }

    public int countAllByOwnerId(long ownerId, String keyword) {
        String sqlCountByOwnerId = sqlCount + " where um.user_id = " + ownerId;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlCountByOwnerId += " where upper(u.username) like CONCAT('%" + keyword + "%') or upper(m.name) like CONCAT('%" + keyword + "%') or upper(c.name) like CONCAT('%" + keyword + "%') ";
        }
        return jdbcTemplate.queryForObject(sqlCountByOwnerId, Integer.class);
    }

    private RowMapper<CategoryDto> categoryRowMapper() {
        return (rs, rowNum) -> {
            CategoryDto c = new CategoryDto();
            c.setId(rs.getLong("id"));
            c.setName(rs.getString("name"));
            c.setDescription(rs.getString("description"));
            c.setMerchantId(rs.getLong("merchant_id"));
            c.setMerchantName(rs.getString("merchant_name"));
            return c;
        };
    }

    public CategoryDto findById(Long id) {
        String sqlFindById = sql + " where c.id = ? ";
        CategoryDto categoryDto = jdbcTemplate.queryForObject(
                sqlFindById,
                categoryRowMapper(),
                id
        );
        return categoryDto;
    }

    public void insert(CategoryDto category) {
        String sqlInsert = "INSERT INTO umkm.category (merchant_id, name, description, status, created_by, " +
                "updated_by) " +
                "VALUES (?, ?, ?, ?, ?, ?) ";

        jdbcTemplate.update(
                sqlInsert,
                category.getMerchantId(),
                category.getName(),
                category.getDescription(),
                category.getStatus(),
                category.getCreatedBy(),
                category.getUpdatedBy()
        );
    }

    public void update(CategoryDto category) {
        String sql = "update umkm.category " +
                "set merchant_id = ?, name = ?, description = ?, status = ?, updated_by = ?, " +
                "where id = ? ";
        jdbcTemplate.update(
                sql,
                category.getMerchantId(),
                category.getName(),
                category.getDescription(),
                category.getStatus(),
                category.getUpdatedBy(),
                category.getId()
        );
    }
}
