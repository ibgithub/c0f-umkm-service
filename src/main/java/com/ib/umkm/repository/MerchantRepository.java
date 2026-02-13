package com.ib.umkm.repository;

import com.ib.umkm.dto.MerchantDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MerchantRepository {

    private final JdbcTemplate jdbcTemplate;

    public MerchantRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<MerchantDto> findAll() {
        String sql = "SELECT m.id, m.created_by, m.created_date, m.updated_by, m.updated_date, " +
                "m.name, m.status, u.username owner_name " +
                "from umkm.merchant m " +
                "inner join umkm.user_merchant um on um.merchant_id = m.id " +
                "inner join auth.users u on um.user_id = u.id "
        ;

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
}
