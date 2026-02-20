package com.ib.umkm.repository;

import com.ib.umkm.dto.MerchantDto;
import com.ib.umkm.util.Constants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MerchantRepository {
    private final JdbcTemplate jdbcTemplate;
    String sql = "SELECT m.id, m.created_by, m.created_date, m.updated_by, m.updated_date, " +
            "m.name, m.status, u.username owner_name, um.user_id owner_id " +
            "from umkm.merchant m " +
            "inner join umkm.user_merchant um on um.merchant_id = m.id " +
            "inner join auth.users u on um.user_id = u.id "
            ;

    public MerchantRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<MerchantDto> findAll() {

        return jdbcTemplate.query(sql, merchantRowMapper());
    }

    private RowMapper<MerchantDto> merchantRowMapper() {
        return (rs, rowNum) -> {
            MerchantDto m = new MerchantDto();
            m.setId(rs.getLong("id"));
            m.setName(rs.getString("name"));
            m.setOwnerName(rs.getString("owner_name"));
            m.setOwnerId(rs.getLong("owner_id"));

            m.setCreatedBy(rs.getString("created_by"));
            m.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
            if (rs.getTimestamp("updated_date") != null) {
                m.setUpdatedDate(rs.getTimestamp("updated_date").toLocalDateTime());
            }
            m.setUpdatedBy(rs.getString("updated_by"));
            return m;
        };
    }

    public MerchantDto findById(Long id) {
        String sqlFindById = sql + " where m.id = ? ";
        MerchantDto merchantDto = jdbcTemplate.queryForObject(
                sqlFindById,
                merchantRowMapper(),
                id
        );
        return merchantDto;
    }

    public List<MerchantDto> findByOwnerId(Long userId) {
        String sqlFindByOwnerId = sql + " where um.user_id = ? ";
        List<MerchantDto> merchants = jdbcTemplate.query(
                sqlFindByOwnerId,
                merchantRowMapper(),
                userId
        );
        return merchants;
    }

    public void insert(MerchantDto merchant) {
        String sqlInsert = "INSERT INTO umkm.merchant (name, created_by) " +
                "VALUES (?, ?) RETURNING id";

        Long merchantId = jdbcTemplate.queryForObject(
                sqlInsert,
                Long.class,
                merchant.getName(),
                merchant.getCreatedBy()
        );

        String sqlInsertUserMerchant = "INSERT INTO umkm.user_merchant (user_id, merchant_id, business_role, created_by) " +
                "VALUES (?, ?, ?, ? )";

        jdbcTemplate.update(
                sqlInsertUserMerchant,
                merchant.getOwnerId(),
                merchantId,
                Constants.OWNER,
                merchant.getCreatedBy()
        );
    }

    public void update(MerchantDto merchant) {
        String sql = "update umkm.merchant " +
                "set name = ?, updated_by = ?, updated_date = now() " +
                "where id = ? ";
        jdbcTemplate.update(
                sql,
                merchant.getName(),
                merchant.getUpdatedBy(),
                merchant.getId()
        );

        String sqlUpdateUserMerchant = "UPDATE umkm.user_merchant set user_id = ?, updated_by = ?, updated_date = now() " +
                "WHERE merchant_id = ? ";

        jdbcTemplate.update(
                sqlUpdateUserMerchant,
                merchant.getOwnerId(),
                merchant.getUpdatedBy(),
                merchant.getId()
        );
    }
}
