package com.pretest.shop.customer;

import com.pretest.shop.api.ApiModels.CreateCustomerRequest;
import com.pretest.shop.api.ApiModels.CustomerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomerService {

    private final JdbcTemplate jdbcTemplate;

    public CustomerService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        Long id = jdbcTemplate.queryForObject(
                "INSERT INTO pelanggan (nama, email, no_telepon) VALUES (?, ?, ?) RETURNING id",
                Long.class,
                request.nama(),
                request.email(),
                request.noTelepon());
        return getCustomer(id);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(long id) {
        return jdbcTemplate.query(
                        """
                        SELECT id, nama, email, no_telepon, created_at
                        FROM pelanggan
                        WHERE id = ?
                        """,
                        (rs, rowNum) -> new CustomerResponse(
                                rs.getLong("id"),
                                rs.getString("nama"),
                                rs.getString("email"),
                                rs.getString("no_telepon"),
                                rs.getTimestamp("created_at").toLocalDateTime()),
                        id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pelanggan tidak ditemukan"));
    }

    @Transactional(readOnly = true)
    public void requireCustomer(long id) {
        Boolean exists = jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM pelanggan WHERE id = ?)", Boolean.class, id);
        if (!Boolean.TRUE.equals(exists)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pelanggan tidak ditemukan");
        }
    }
}
