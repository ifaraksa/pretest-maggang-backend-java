package com.pretest.shop.transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.pretest.shop.api.ApiModels.TransactionDetailResponse;
import com.pretest.shop.api.ApiModels.TransactionItemResponse;
import com.pretest.shop.api.ApiModels.TransactionSummaryResponse;
import com.pretest.shop.cart.CartService;
import com.pretest.shop.cart.CartService.CheckoutItem;
import com.pretest.shop.catalog.CatalogService;
import com.pretest.shop.customer.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TransactionService {

    private final JdbcTemplate jdbcTemplate;
    private final CartService cartService;
    private final CatalogService catalogService;
    private final CustomerService customerService;

    public TransactionService(
            JdbcTemplate jdbcTemplate,
            CartService cartService,
            CatalogService catalogService,
            CustomerService customerService) {
        this.jdbcTemplate = jdbcTemplate;
        this.cartService = cartService;
        this.catalogService = catalogService;
        this.customerService = customerService;
    }

    @Transactional
    public TransactionDetailResponse checkout(long customerId) {
        List<CheckoutItem> items = cartService.lockItemsForCheckout(customerId);
        BigDecimal total = BigDecimal.ZERO;
        for (CheckoutItem item : items) {
            if (item.quantity() > item.product().stok()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Stok barang " + item.product().nama() + " tidak mencukupi");
            }
            total = total.add(item.product().harga().multiply(BigDecimal.valueOf(item.quantity())));
        }

        for (CheckoutItem item : items) {
            catalogService.decreaseStock(item.product().id(), item.quantity());
        }

        String code = "TRX-" + UUID.randomUUID();
        Long transactionId = jdbcTemplate.queryForObject(
                """
                INSERT INTO transaksi (kode_transaksi, pelanggan_id, total_harga, status)
                VALUES (?, ?, ?, 'DIPROSES')
                RETURNING id
                """,
                Long.class,
                code,
                customerId,
                total);

        for (CheckoutItem item : items) {
            BigDecimal price = item.product().harga();
            jdbcTemplate.update(
                    """
                    INSERT INTO detail_transaksi
                        (transaksi_id, barang_id, nama_barang, harga_satuan, jumlah, subtotal)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """,
                    transactionId,
                    item.product().id(),
                    item.product().nama(),
                    price,
                    item.quantity(),
                    price.multiply(BigDecimal.valueOf(item.quantity())));
        }

        cartService.clearAfterCheckout(customerId);
        return getTransaction(transactionId);
    }

    @Transactional(readOnly = true)
    public List<TransactionSummaryResponse> getCustomerTransactions(long customerId) {
        customerService.requireCustomer(customerId);
        return jdbcTemplate.query(
                """
                SELECT t.id, t.kode_transaksi, t.total_harga, t.status, t.created_at,
                       COALESCE(di.jumlah_barang, 0) AS jumlah_barang,
                       COALESCE(di.ringkasan_barang, '') AS ringkasan_barang
                FROM transaksi t
                LEFT JOIN LATERAL (
                    SELECT COUNT(*) AS jumlah_barang,
                           string_agg(dt.nama_barang, ', ' ORDER BY dt.id) AS ringkasan_barang
                    FROM detail_transaksi dt
                    WHERE dt.transaksi_id = t.id
                ) di ON TRUE
                WHERE t.pelanggan_id = ?
                ORDER BY t.created_at DESC, t.id DESC
                """,
                (rs, rowNum) -> new TransactionSummaryResponse(
                        rs.getLong("id"),
                        rs.getString("kode_transaksi"),
                        rs.getBigDecimal("total_harga"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getInt("jumlah_barang"),
                        rs.getString("ringkasan_barang")),
                customerId);
    }

    @Transactional(readOnly = true)
    public TransactionDetailResponse getTransaction(long transactionId) {
        List<TransactionHeader> headers = jdbcTemplate.query(
                """
                SELECT id, kode_transaksi, pelanggan_id, total_harga, status, created_at
                FROM transaksi
                WHERE id = ?
                """,
                (rs, rowNum) -> new TransactionHeader(
                        rs.getLong("id"),
                        rs.getString("kode_transaksi"),
                        rs.getLong("pelanggan_id"),
                        rs.getBigDecimal("total_harga"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at").toLocalDateTime()),
                transactionId);
        TransactionHeader header = headers.stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaksi tidak ditemukan"));

        List<TransactionItemResponse> items = jdbcTemplate.query(
                """
                SELECT barang_id, nama_barang, harga_satuan, jumlah, subtotal
                FROM detail_transaksi
                WHERE transaksi_id = ?
                ORDER BY id
                """,
                (rs, rowNum) -> new TransactionItemResponse(
                        rs.getLong("barang_id"),
                        rs.getString("nama_barang"),
                        rs.getBigDecimal("harga_satuan"),
                        rs.getInt("jumlah"),
                        rs.getBigDecimal("subtotal")),
                transactionId);
        return new TransactionDetailResponse(
                header.id(),
                header.code(),
                header.customerId(),
                header.total(),
                header.status(),
                header.createdAt(),
                items);
    }

    private record TransactionHeader(
            long id,
            String code,
            long customerId,
            BigDecimal total,
            String status,
            java.time.LocalDateTime createdAt) {
    }
}
