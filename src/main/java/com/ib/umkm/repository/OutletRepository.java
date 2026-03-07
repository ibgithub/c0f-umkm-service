package com.ib.umkm.repository;

import com.ib.umkm.dto.OutletDto;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OutletRepository {
    private final JdbcTemplate jdbcTemplate;
    String sql = "select o.id, o.merchant_id, o.name, o.address, m.name as merchant_name, " +
            "o.status, u.first_name, u.last_name " +
            "from umkm.outlet o " +
            "inner join umkm.merchant m on m.id = o.merchant_id " +
            "inner join umkm.user_merchant um on um.merchant_id = m.id " +
            "inner join auth.users u on um.user_id = u.id "
            ;
    String sqlCount = "SELECT COUNT(1) " +
            "from umkm.outlet o " +
            "inner join umkm.merchant m on m.id = o.merchant_id " +
            "inner join umkm.user_merchant um on um.merchant_id = m.id " +
            "inner join auth.users u on um.user_id = u.id "
            ;
    String order_by = " order by m.name, o.name ";

    public OutletRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<OutletDto> findAll() {
        return jdbcTemplate.query(sql, outletRowMapper());
    }
    public List<OutletDto> findByOwnerId(Long userId) {
        String sqlFindByOwnerId = sql + " where um.user_id = ? " + order_by;
        List<OutletDto> outlets = jdbcTemplate.query(
                sqlFindByOwnerId,
                outletRowMapper(),
                userId
        );
        return outlets;
    }
    public List<OutletDto> findByMerchantId(Long merchantId) {
        String sqlFindByOwnerId = "select o.id, o.name from umkm.outlet o " +
                "inner join umkm.merchant m on m.id = o.merchant_id " +
                "where o.merchant_id = ? " + order_by;
        List<OutletDto> outlets = jdbcTemplate.query(
                sqlFindByOwnerId,
                outletLabelRowMapper(),
                merchantId
        );
        return outlets;
    }

    public List<OutletDto> findAll(int limit, int offset, String keyword) {
        String sqlSelect = sql;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlSelect += " where upper(m.name) like CONCAT('%', ?, '%') or upper(o.name) like CONCAT('%', ?, '%') or upper(o.address) like CONCAT('%', ?, '%') " +
                    order_by +
                    " LIMIT ? OFFSET ? ";
            return jdbcTemplate.query(sqlSelect,
                    new BeanPropertyRowMapper<>(OutletDto.class),
                    keyword, keyword, keyword,
                    limit, offset);
        }
        sqlSelect += order_by + " LIMIT ? OFFSET ? ";

        return jdbcTemplate.query(sqlSelect,
                new BeanPropertyRowMapper<>(OutletDto.class),
                limit, offset);
    }

    public List<OutletDto> findByOwnerId(int limit, int offset, Long userId, String keyword) {
        String sqlSelect = sql;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlSelect += " where ( upper(m.name) like CONCAT('%', ?, '%') or upper(o.name) like CONCAT('%', ?, '%') or upper(o.address) like CONCAT('%', ?, '%') ) " +
                    "and um.user_id = ? " +
                    order_by +
                    " LIMIT ? OFFSET ? ";
            return jdbcTemplate.query(sqlSelect,
                    new BeanPropertyRowMapper<>(OutletDto.class),
                    keyword, keyword, keyword,
                    userId,
                    limit, offset );
        }
        String sqlFindByOwnerId = sql + " where um.user_id = ? " + order_by + " LIMIT ? OFFSET ? " ;
        return jdbcTemplate.query(sqlFindByOwnerId,
                new BeanPropertyRowMapper<>(OutletDto.class),
                userId,
                limit, offset);
    }

    public int countAll(String keyword) {
        String sqlSelectCount = sqlCount;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlSelectCount += " where upper(m.name) like CONCAT('%" + keyword + "%') or upper(o.name) like CONCAT('%" + keyword + "%') ";
            return jdbcTemplate.queryForObject(sqlSelectCount, Integer.class);
        }
        return jdbcTemplate.queryForObject(sqlSelectCount, Integer.class);
    }

    public int countAllByOwnerId(long ownerId, String keyword) {
        String sqlCountByOwnerId = sqlCount + " where um.user_id = " + ownerId;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlCountByOwnerId += " and upper(u.username) like CONCAT('%" + keyword + "%') or upper(m.name) like CONCAT('%" + keyword + "%') or upper(o.name) like CONCAT('%" + keyword + "%') ";
        }
        return jdbcTemplate.queryForObject(sqlCountByOwnerId, Integer.class);
    }

    private RowMapper<OutletDto> outletRowMapper() {
        return (rs, rowNum) -> {
            OutletDto dto = new OutletDto();
            dto.setId(rs.getLong("id"));
            dto.setName(rs.getString("name"));
            dto.setMerchantId(rs.getLong("merchant_id"));
            dto.setMerchantName(rs.getString("merchant_name"));
            dto.setAddress(rs.getString("address"));
            dto.setStatus(rs.getString("status"));
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            firstName = (firstName == null ? "" : firstName);
            lastName = (lastName == null ? "" : lastName);
            String fullName = firstName + (lastName.isEmpty() ? "" : " " + lastName);
            dto.setOwnerName(fullName);
            return dto;
        };
    }
    private RowMapper<OutletDto> outletLabelRowMapper() {
        return (rs, rowNum) -> {
            OutletDto dto = new OutletDto();
            dto.setId(rs.getLong("id"));
            dto.setName(rs.getString("name"));
            return dto;
        };
    }
    public OutletDto findById(Long id) {
        String sqlFindById = sql + " where o.id = ? ";
        OutletDto outletDto = jdbcTemplate.queryForObject(
                sqlFindById,
                outletRowMapper(),
                id
        );
        return outletDto;
    }

    public void insert(OutletDto outlet) {
        String sqlInsert = "INSERT INTO umkm.outlet (merchant_id, name, address, status, created_by, " +
                "updated_by) " +
                "VALUES (?, ?, ?, ?, ?, ?) ";
        jdbcTemplate.update(
                sqlInsert,
                outlet.getMerchantId(),
                outlet.getName(),
                outlet.getAddress(),
                outlet.getStatus(),
                outlet.getCreatedBy(),
                outlet.getUpdatedBy()
        );
    }

    public void update(OutletDto outlet) {
        String sql = "update umkm.outlet " +
                "set merchant_id = ?, name = ?, address = ?, status = ?, updated_by = ? " +
                "where id = ? ";
        jdbcTemplate.update(
                sql,
                outlet.getMerchantId(),
                outlet.getName(),
                outlet.getAddress(),
                outlet.getStatus(),
                outlet.getUpdatedBy(),
                outlet.getId()
        );
    }
}
