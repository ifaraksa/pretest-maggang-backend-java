package com.pretest.shop.catalog;

import java.math.BigDecimal;
import java.util.List;

import com.pretest.shop.api.ApiModels.CreateProductRequest;
import com.pretest.shop.api.ApiModels.CreateStoreRequest;
import com.pretest.shop.api.ApiModels.ProductResponse;
import com.pretest.shop.api.ApiModels.StoreResponse;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CatalogService {

    private static final String PRODUCT_SELECT = """
            SELECT b.id, b.toko_id, t.nama AS toko_nama, b.nama, b.deskripsi,
                   b.harga, b.stok, b.created_at, b.updated_at
            FROM barang b
            JOIN toko t ON t.id = b.toko_id
            """;

    private final JdbcTemplate jdbcTemplate;

    public CatalogService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public StoreResponse createStore(CreateStoreRequest request) {
        Long id = jdbcTemplate.queryForObject(
                "INSERT INTO toko (nama, deskripsi, kota) VALUES (?, ?, ?) RETURNING id",
                Long.class,
                request.nama(),
                request.deskripsi(),
                request.kota());
        return getStore(id);
    }

    @Transactional
    public ProductResponse createProduct(long storeId, CreateProductRequest request) {
        requireStore(storeId);
        Long id = jdbcTemplate.queryForObject(
                "INSERT INTO barang (toko_id, nama, deskripsi, harga, stok) VALUES (?, ?, ?, ?, ?) RETURNING id",
                Long.class,
                storeId,
                request.nama(),
                request.deskripsi(),
                request.harga(),
                request.stok());
        return getProduct(id);
    }

    @Transactional
    public ProductResponse updateProduct(long productId, CreateProductRequest request) {
        int updated = jdbcTemplate.update(
                """
                UPDATE barang
                SET nama = ?, deskripsi = ?, harga = ?, stok = ?, updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """,
                request.nama(),
                request.deskripsi(),
                request.harga(),
                request.stok(),
                productId);
        if (updated != 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Barang tidak ditemukan");
        }
        return getProduct(productId);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String keyword, Long storeId) {
        StringBuilder sql = new StringBuilder(PRODUCT_SELECT);
        boolean hasWhere = false;
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" WHERE (b.nama ILIKE ? OR COALESCE(b.deskripsi, '') ILIKE ?)");
            hasWhere = true;
        }
        if (storeId != null) {
            sql.append(hasWhere ? " AND" : " WHERE").append(" b.toko_id = ?");
        }
        sql.append(" ORDER BY b.id");

        if (keyword != null && !keyword.isBlank() && storeId != null) {
            String search = "%" + keyword.trim() + "%";
            return jdbcTemplate.query(sql.toString(), this::mapProduct, search, search, storeId);
        }
        if (keyword != null && !keyword.isBlank()) {
            String search = "%" + keyword.trim() + "%";
            return jdbcTemplate.query(sql.toString(), this::mapProduct, search, search);
        }
        if (storeId != null) {
            return jdbcTemplate.query(sql.toString(), this::mapProduct, storeId);
        }
        return jdbcTemplate.query(sql.toString(), this::mapProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(long id) {
        return jdbcTemplate.query(
                        PRODUCT_SELECT + " WHERE b.id = ?",
                        this::mapProduct,
                        id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Barang tidak ditemukan"));
    }

    @Transactional
    public ProductResponse getProductForUpdate(long id) {
        return jdbcTemplate.query(
                        PRODUCT_SELECT + " WHERE b.id = ? FOR UPDATE OF b",
                        this::mapProduct,
                        id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Barang tidak ditemukan"));
    }

    @Transactional
    public void decreaseStock(long productId, int quantity) {
        int updated = jdbcTemplate.update(
                "UPDATE barang SET stok = stok - ?, updated_at = CURRENT_TIMESTAMP WHERE id = ? AND stok >= ?",
                quantity, productId, quantity);
        if (updated != 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Stok barang tidak mencukupi");
        }
    }

    @Transactional(readOnly = true)
    public StoreResponse getStore(long id) {
        return jdbcTemplate.query(
                        "SELECT id, nama, deskripsi, kota, created_at FROM toko WHERE id = ?",
                        (rs, rowNum) -> new StoreResponse(
                                rs.getLong("id"),
                                rs.getString("nama"),
                                rs.getString("deskripsi"),
                                rs.getString("kota"),
                                rs.getTimestamp("created_at").toLocalDateTime()),
                        id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Toko tidak ditemukan"));
    }

    @Transactional(readOnly = true)
    public void requireStore(long id) {
        Boolean exists = jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM toko WHERE id = ?)", Boolean.class, id);
        if (!Boolean.TRUE.equals(exists)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Toko tidak ditemukan");
        }
    }

    private ProductResponse mapProduct(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new ProductResponse(
                rs.getLong("id"),
                rs.getLong("toko_id"),
                rs.getString("toko_nama"),
                rs.getString("nama"),
                rs.getString("deskripsi"),
                rs.getBigDecimal("harga"),
                rs.getInt("stok"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime());
    }
}
