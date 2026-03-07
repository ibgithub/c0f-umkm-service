package com.ib.umkm.repository;

import com.ib.umkm.dto.OutletStaffDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OutletStaffRepository {
    private final JdbcTemplate jdbcTemplate;
    String sql = "select um.id user_merchant_id, m.id merchant_id, m.name merchant_name, o.id outlet_id, o.name outlet_name, " +
            "u.id user_id, u.username, u.first_name, u.last_name, um.business_role, " +
            "um.status " +
            "from umkm.user_merchant um " +
            "inner join umkm.merchant m on m.id = um.merchant_id " +
            "left join umkm.outlet o on o.merchant_id = m.id " +
            "inner join auth.users u on um.user_id = u.id "
            ;
    String sqlCount = "SELECT COUNT(1) " +
            "from umkm.user_merchant um " +
            "inner join umkm.merchant m on m.id = um.merchant_id " +
            "left join umkm.outlet o on o.merchant_id = m.id " +
            "inner join auth.users u on um.user_id = u.id "
            ;
    String order_by = " order by m.name, o.name, u.first_name, u.last_name ";

    public OutletStaffRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<OutletStaffDto> findAll() {
        return jdbcTemplate.query(sql, outletStaffRowMapper());
    }
    public List<OutletStaffDto> findByOwnerId(Long userId) {
        String sqlFindByOwnerId = sql + " where um.merchant_id in " +
                "(select um2.merchant_id from umkm.user_merchant um2 " +
                "where um2.business_role = 'OWNER' and um2.user_id = ? ) " + order_by;
        List<OutletStaffDto> outletStaffs = jdbcTemplate.query(
                sqlFindByOwnerId,
                outletStaffRowMapper(),
                userId
        );
        return outletStaffs;
    }

    public List<OutletStaffDto> findAll(int limit, int offset, String keyword) {
        String sqlSelect = sql;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlSelect += " where ( upper(m.name) like CONCAT('%', ?, '%') or upper(o.name) like CONCAT('%', ?, '%') or upper(u.username) like CONCAT('%', ?, '%') or upper(u.first_name) like CONCAT('%', ?, '%') or upper(u.last_name) like CONCAT('%', ?, '%') ) " +
                    order_by +
                    " LIMIT ? OFFSET ? ";
            return jdbcTemplate.query(sqlSelect,
                    outletStaffRowMapper(),
                    keyword, keyword, keyword, keyword, keyword,
                    limit, offset);
        }
        sqlSelect += order_by + " LIMIT ? OFFSET ? ";

        return jdbcTemplate.query(sqlSelect,
                outletStaffRowMapper(),
                limit, offset);
    }

    public List<OutletStaffDto> findByOwnerId(int limit, int offset, Long userId, String keyword) {
        String sqlSelect = sql;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlSelect += " where (upper(m.name) like CONCAT('%', ?, '%') or upper(o.name) like CONCAT('%', ?, '%') or upper(u.username) like CONCAT('%', ?, '%') or upper(u.first_name) like CONCAT('%', ?, '%') or upper(u.last_name) like CONCAT('%', ?, '%') " +
                    " and o.id in (select id from umkm.outlet where um.user_id = ? and um.business_role = 'OWNER') " +
                    order_by +
                    " LIMIT ? OFFSET ? ";
            return jdbcTemplate.query(sqlSelect,
                    outletStaffRowMapper(),
                    keyword, keyword, keyword, keyword, keyword,
                    userId,
                    limit, offset );
        }
        String sqlFindByOwnerId = sql + " where um.merchant_id in " +
                "(select um2.merchant_id from umkm.user_merchant um2 " +
                "where um2.business_role = 'OWNER' and um2.user_id = ? ) " + order_by + " LIMIT ? OFFSET ? " ;
        return jdbcTemplate.query(sqlFindByOwnerId,
                outletStaffRowMapper(),
                userId,
                limit, offset);
    }

    public int countAll(String keyword) {
        String sqlSelectCount = sqlCount;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlSelectCount += " where ( upper(m.name) like CONCAT('%" + keyword + "%') or upper(o.name) like CONCAT('%" + keyword + "%') or upper(u.username) like CONCAT('%" + keyword + "%') or upper(u.first_name) like CONCAT('%" + keyword + "%') or upper(u.last_name) like CONCAT('%" + keyword + "%') ) ";
            return jdbcTemplate.queryForObject(sqlSelectCount, Integer.class);
        }
        return jdbcTemplate.queryForObject(sqlSelectCount, Integer.class);
    }

    public int countAllByOwnerId(long ownerId, String keyword) {
        String sqlCountByOwnerId = sqlCount + " where um.merchant_id in " +
                "(select um2.merchant_id from umkm.user_merchant um2 " +
                "where um2.business_role = 'OWNER' and um2.user_id = "+ ownerId + ") " ;
        if (keyword != null && !keyword.equals("")) {
            keyword = keyword.toUpperCase();
            sqlCountByOwnerId += " and ( upper(m.name) like CONCAT('%" + keyword + "%') or upper(o.name) like CONCAT('%" + keyword + "%') or upper(u.username) like CONCAT('%" + keyword + "%') or upper(u.first_name) like CONCAT('%" + keyword + "%') or upper(u.last_name) like CONCAT('%" + keyword + "%') )  ";
        }
        return jdbcTemplate.queryForObject(sqlCountByOwnerId, Integer.class);
    }
    private RowMapper<OutletStaffDto> outletStaffRowMapper() {
        return (rs, rowNum) -> {
            OutletStaffDto dto = new OutletStaffDto();
            dto.setId(rs.getLong("user_merchant_id"));
            dto.setMerchantId(rs.getLong("merchant_id"));
            dto.setMerchantName(rs.getString("merchant_name"));
            dto.setOutletId(rs.getLong("outlet_id"));
            dto.setOutletName(rs.getString("outlet_name"));
            dto.setUserId(rs.getLong("user_id"));
            dto.setUsername(rs.getString("outlet_name"));

            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            firstName = (firstName == null ? "" : firstName);
            lastName = (lastName == null ? "" : lastName);
            String fullName = firstName + (lastName.isEmpty() ? "" : " " + lastName);
            dto.setFullName(fullName);
            dto.setBusinessRole(rs.getString("business_role"));
            dto.setStatus(rs.getString("status"));
            return dto;
        };
    }

    public OutletStaffDto findById(Long id) {
        String sqlFindById = sql + " where um.id = ? ";
        OutletStaffDto outletStaffDto = jdbcTemplate.queryForObject(
                sqlFindById,
                outletStaffRowMapper(),
                id
        );
        return outletStaffDto;
    }

    public void insert(OutletStaffDto outletStaff) {
        String sqlInsert = "INSERT INTO umkm.user_merchant (user_id, merchant_id, outlet_id, business_role, status, " +
                "created_by, updated_by) " +
                "VALUES (?, ?, ?, ?, ?, " +
                "?, ?) ";
        jdbcTemplate.update(
                sqlInsert,
                outletStaff.getUserId(),
                outletStaff.getMerchantId(),
                outletStaff.getOutletId(),
                outletStaff.getBusinessRole(),
                outletStaff.getStatus(),
                outletStaff.getCreatedBy(),
                outletStaff.getUpdatedBy()
        );
    }

    public void update(OutletStaffDto outletStaff) {
        String sql = "update umkm.user_merchant " +
                "set user_id = ?, merchant_id = ?, outlet_id = ?, business_role = ?, status = ?, " +
                "updated_by = ? " +
                "where id = ? ";
        jdbcTemplate.update(
                sql,
                outletStaff.getUserId(),
                outletStaff.getMerchantId(),
                outletStaff.getOutletId(),
                outletStaff.getBusinessRole(),
                outletStaff.getStatus(),
                outletStaff.getUpdatedBy(),
                outletStaff.getId()
        );
    }
}
