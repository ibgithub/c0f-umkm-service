package com.ib.umkm.repository;

import com.ib.umkm.dto.MerchantDto;
import com.ib.umkm.util.Constants;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MerchantRepository {
    private final JdbcTemplate jdbcTemplate;
    String sql = "SELECT m.id, m.created_by, m.created_at, m.updated_by, m.updated_at, " +
            "m.name, m.status, u.first_name, u.last_name, um.user_id owner_id " +
            "from umkm.merchant m " +
            "inner join umkm.user_merchant um on um.merchant_id = m.id " +
            "inner join auth.users u on um.user_id = u.id "
            ;
    String sqlCount = "SELECT COUNT(1) " +
            "from umkm.merchant m " +
            "inner join umkm.user_merchant um on um.merchant_id = m.id " +
            "inner join auth.users u on um.user_id = u.id "
            ;
    String order_by = " order by m.name ";
    public MerchantRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<MerchantDto> findAll() {
        return jdbcTemplate.query(sql + order_by, merchantRowMapper);
    }
    public List<MerchantDto> findByOwnerId(Long userId) {
        String sqlFindByOwnerId = sql + " where um.user_id = ? " + order_by;
        List<MerchantDto> merchants = jdbcTemplate.query(
                sqlFindByOwnerId,
                merchantRowMapper,
                userId
        );
        return merchants;
    }

    public List<MerchantDto> findAll(int limit, int offset, String keyword) {
        String sqlSelect = sql;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlSelect += " where upper(u.first_name) like CONCAT('%', ?, '%') or upper(u.last_name) like CONCAT('%', ?, '%') or upper(m.name) like CONCAT('%', ?, '%') " +
                    order_by +
                    " LIMIT ? OFFSET ? ";
            return jdbcTemplate.query(sqlSelect,
                    merchantRowMapper,
                    keyword, keyword, keyword,
                    limit, offset);
        }
        sqlSelect += order_by + " LIMIT ? OFFSET ? " ;

        return jdbcTemplate.query(sqlSelect,
                merchantRowMapper,
                limit, offset);
    }
    public List<MerchantDto> findByOwnerId(int limit, int offset, Long userId, String keyword) {
        String sqlSelect = sql;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlSelect += " where (upper(u.username) like CONCAT('%', ?, '%') or m.name like CONCAT('%', ?, '%')) " +
                    "AND um.user_id = ? " +
                    order_by +
                    " LIMIT ? OFFSET ? where ";
            return jdbcTemplate.query(sqlSelect,
                    new BeanPropertyRowMapper<>(MerchantDto.class),
                    keyword, keyword, userId,
                    limit, offset);
        }

        String sqlFindByOwnerId = sql + " where um.user_id = ? " + order_by + " LIMIT ? OFFSET ? " ;
        return jdbcTemplate.query(sqlFindByOwnerId,
                new BeanPropertyRowMapper<>(MerchantDto.class),
                userId,
                limit, offset);
    }

    public int countAll(String keyword) {
        String sqlSelectCount = sqlCount;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlSelectCount += " where upper(u.username) like CONCAT('%" + keyword + "%') or upper(m.name) like CONCAT('%" + keyword + "%') ";
            return jdbcTemplate.queryForObject(sqlSelectCount, Integer.class);
        }
        return jdbcTemplate.queryForObject(sqlSelectCount, Integer.class);
    }

    public int countAllByOwnerId(long ownerId, String keyword) {
        String sqlCountByOwnerId = sqlCount + " where um.user_id = " + ownerId + order_by;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlCountByOwnerId += " and um.user_id = " + ownerId + " and ( upper(u.username) like CONCAT('%" + keyword + "%') or upper(m.name) like CONCAT('%" + keyword + "% ') ) " + order_by;
        }
        return jdbcTemplate.queryForObject(sqlCountByOwnerId, Integer.class);
    }

    private final RowMapper<MerchantDto> merchantRowMapper = (rs, rowNum) -> {
        MerchantDto m = new MerchantDto();
        m.setId(rs.getLong("id"));
        m.setName(rs.getString("name"));
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        firstName = (firstName == null ? "" : firstName);
        lastName = (lastName == null ? "" : lastName);
        String fullName = firstName + (lastName.isEmpty() ? "" : " " + lastName);
        m.setOwnerName(fullName);
        m.setOwnerId(rs.getLong("owner_id"));

        m.setCreatedBy(rs.getString("created_by"));
        m.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        if (rs.getTimestamp("updated_at") != null) {
            m.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        m.setUpdatedBy(rs.getString("updated_by"));
        return m;
    };

    public MerchantDto findById(Long id) {
        String sqlFindById = sql + " where m.id = ? ";
        MerchantDto merchantDto = jdbcTemplate.queryForObject(
                sqlFindById,
                merchantRowMapper,
                id
        );
        return merchantDto;
    }

    public void insert(MerchantDto merchant) {
        String sqlInsert = "INSERT INTO umkm.merchant (name, created_by, updated_by) " +
                "VALUES (?, ?, ?) RETURNING id";

        Long merchantId = jdbcTemplate.queryForObject(
                sqlInsert,
                Long.class,
                merchant.getName(),
                merchant.getCreatedBy(),
                merchant.getCreatedBy()
        );

        String sqlInsertUserMerchant = "INSERT INTO umkm.user_merchant (user_id, merchant_id, business_role, created_by, updated_by) " +
                "VALUES (?, ?, ?, ?, ? )";

        jdbcTemplate.update(
                sqlInsertUserMerchant,
                merchant.getOwnerId(),
                merchantId,
                Constants.OWNER,
                merchant.getCreatedBy(),
                merchant.getCreatedBy()
        );
    }

    public void update(MerchantDto merchant) {
        String sql = "update umkm.merchant " +
                "set name = ?, updated_by = ?, updated_at = now() " +
                "where id = ? ";
        jdbcTemplate.update(
                sql,
                merchant.getName(),
                merchant.getUpdatedBy(),
                merchant.getId()
        );

        String sqlUpdateUserMerchant = "UPDATE umkm.user_merchant set user_id = ?, updated_by = ?, updated_at = now() " +
                "WHERE merchant_id = ? ";

        jdbcTemplate.update(
                sqlUpdateUserMerchant,
                merchant.getOwnerId(),
                merchant.getUpdatedBy(),
                merchant.getId()
        );
    }
}
