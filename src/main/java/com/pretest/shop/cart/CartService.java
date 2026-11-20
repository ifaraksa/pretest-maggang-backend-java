package com.pretest.shop.cart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pretest.shop.api.ApiModels.CartLineResponse;
import com.pretest.shop.api.ApiModels.CartResponse;
import com.pretest.shop.api.ApiModels.ProductResponse;
import com.pretest.shop.catalog.CatalogService;
import com.pretest.shop.customer.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CartService {

    private final JdbcTemplate jdbcTemplate;
    private final CatalogService catalogService;
    private final CustomerService customerService;

    public CartService(
            JdbcTemplate jdbcTemplate,
            CatalogService catalogService,
            CustomerService customerService) {
        this.jdbcTemplate = jdbcTemplate;
        this.catalogService = catalogService;
        this.customerService = customerService;
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(long customerId) {
        customerService.requireCustomer(customerId);
        List<CartLineResponse> items = jdbcTemplate.query(
                """
                SELECT b.id AS barang_id, b.nama, b.harga, b.stok, ic.jumlah
                FROM keranjang k
                JOIN item_keranjang ic ON ic.keranjang_id = k.id
                JOIN barang b ON b.id = ic.barang_id
                WHERE k.pelanggan_id = ?
                ORDER BY b.id
                """,
                (rs, rowNum) -> {
                    BigDecimal price = rs.getBigDecimal("harga");
                    int quantity = rs.getInt("jumlah");
                    return new CartLineResponse(
                            rs.getLong("barang_id"),
                            rs.getString("nama"),
                            price,
                            rs.getInt("stok"),
                            quantity,
                            price.multiply(BigDecimal.valueOf(quantity)));
                },
                customerId);
        return toCartResponse(customerId, items);
    }

    @Transactional
    public CartResponse addItem(long customerId, long productId, int quantity) {
        customerService.requireCustomer(customerId);
        ProductResponse product = catalogService.getProduct(productId);
        Long cartId = getOrCreateCart(customerId);

        Integer existingQuantity = jdbcTemplate.query(
                        "SELECT jumlah FROM item_keranjang WHERE keranjang_id = ? AND barang_id = ?",
                        (rs, rowNum) -> rs.getInt("jumlah"),
                        cartId,
                        productId)
                .stream()
                .findFirst()
                .orElse(0);
        if ((long) existingQuantity + quantity > product.stok()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Jumlah melebihi stok barang yang tersedia");
        }

        jdbcTemplate.update(
                """
                INSERT INTO item_keranjang (keranjang_id, barang_id, jumlah)
                VALUES (?, ?, ?)
                ON CONFLICT (keranjang_id, barang_id)
                DO UPDATE SET jumlah = item_keranjang.jumlah + EXCLUDED.jumlah
                """,
                cartId,
                productId,
                quantity);
        return getCart(customerId);
    }

    @Transactional
    public CartResponse setItemQuantity(long customerId, long productId, int quantity) {
        customerService.requireCustomer(customerId);
        ProductResponse product = catalogService.getProduct(productId);
        if (quantity > product.stok()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Jumlah melebihi stok barang yang tersedia");
        }
        int updated = jdbcTemplate.update(
                """
                UPDATE item_keranjang ic
                SET jumlah = ?
                FROM keranjang k
                WHERE ic.keranjang_id = k.id
                  AND k.pelanggan_id = ?
                  AND ic.barang_id = ?
                """,
                quantity,
                customerId,
                productId);
        if (updated != 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Barang tidak ditemukan di keranjang");
        }
        return getCart(customerId);
    }

    @Transactional
    public void removeItem(long customerId, long productId) {
        customerService.requireCustomer(customerId);
        int deleted = jdbcTemplate.update(
                """
                DELETE FROM item_keranjang ic
                USING keranjang k
                WHERE ic.keranjang_id = k.id
                  AND k.pelanggan_id = ?
                  AND ic.barang_id = ?
                """,
                customerId,
                productId);
        if (deleted != 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Barang tidak ditemukan di keranjang");
        }
    }

    @Transactional
    public List<CheckoutItem> lockItemsForCheckout(long customerId) {
        customerService.requireCustomer(customerId);
        List<CartEntry> entries = jdbcTemplate.query(
                """
                SELECT ic.barang_id, ic.jumlah
                FROM keranjang k
                JOIN item_keranjang ic ON ic.keranjang_id = k.id
                WHERE k.pelanggan_id = ?
                ORDER BY ic.barang_id
                """,
                (rs, rowNum) -> new CartEntry(rs.getLong("barang_id"), rs.getInt("jumlah")),
                customerId);
        if (entries.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Keranjang masih kosong");
        }

        List<CheckoutItem> items = new ArrayList<>();
        for (CartEntry entry : entries) {
            ProductResponse product = catalogService.getProductForUpdate(entry.productId());
            items.add(new CheckoutItem(product, entry.quantity()));
        }
        return items;
    }

    @Transactional
    public void clearAfterCheckout(long customerId) {
        jdbcTemplate.update(
                """
                DELETE FROM item_keranjang ic
                USING keranjang k
                WHERE ic.keranjang_id = k.id AND k.pelanggan_id = ?
                """,
                customerId);
    }

    private Long getOrCreateCart(long customerId) {
        return jdbcTemplate.queryForObject(
                """
                INSERT INTO keranjang (pelanggan_id) VALUES (?)
                ON CONFLICT (pelanggan_id) DO UPDATE SET pelanggan_id = EXCLUDED.pelanggan_id
                RETURNING id
                """,
                Long.class,
                customerId);
    }

    private CartResponse toCartResponse(long customerId, List<CartLineResponse> items) {
        BigDecimal total = items.stream()
                .map(CartLineResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(customerId, items, total);
    }

    public record CheckoutItem(ProductResponse product, int quantity) {
    }

    private record CartEntry(long productId, int quantity) {
    }
}
