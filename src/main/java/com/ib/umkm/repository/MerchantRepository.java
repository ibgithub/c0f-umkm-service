package com.ib.umkm.repository;

import com.ib.umkm.dto.MerchantDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MerchantRepository {
    private final JdbcTemplate jdbcTemplate;
    String sql = "SELECT m.id, m.created_by, m.created_date, m.updated_by, m.updated_date, " +
            "m.name, m.status, u.username owner_name " +
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
        sql += " where id = ? ";
        MerchantDto merchantDto = jdbcTemplate.queryForObject(
                sql,
                merchantRowMapper(),
                id
        );
        return merchantDto;
    }

    public int insert(MerchantDto merchant) {
        String sqlInsert = "INSERT INTO umkm.merchant (name, created_by) " +
                "VALUES (?, ?)";
        int rec;
        rec = jdbcTemplate.update(
                sqlInsert,
                merchant.getName(),
                merchant.getCreatedBy()
        );

        return rec;
    }

    public int update(MerchantDto merchant) {
        String sql = "update umkm.merchant " +
                "set name = ?, updated_by = ?, updated_date = now() " +
                "where id = ? ";
        return jdbcTemplate.update(
                sql,
                merchant.getName(),
                merchant.getUpdatedBy(),
                merchant.getId()
        );
    }
}
