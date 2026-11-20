package com.pretest.shop.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;

import java.math.BigDecimal;
import java.util.List;

import com.pretest.shop.api.ApiModels.ProductResponse;
import com.pretest.shop.api.ApiModels.TransactionSummaryResponse;
import com.pretest.shop.cart.CartService;
import com.pretest.shop.cart.CartService.CheckoutItem;
import com.pretest.shop.catalog.CatalogService;
import com.pretest.shop.customer.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.server.ResponseStatusException;

class TransactionServiceTest {

    @Test
    void customerTransactionsIncludeItemSummary() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        CartService cartService = mock(CartService.class);
        CatalogService catalogService = mock(CatalogService.class);
        CustomerService customerService = mock(CustomerService.class);
        TransactionSummaryResponse expected = new TransactionSummaryResponse(
                12L,
                "TRX-DEMO",
                new BigDecimal("189000.00"),
                "DIPROSES",
                java.time.LocalDateTime.of(2026, 11, 20, 10, 0),
                1,
                "Headset Bluetooth");
        when(jdbcTemplate.query(
                contains("string_agg"),
                any(RowMapper.class),
                eq(7L)))
                .thenReturn(List.of(expected));
        TransactionService service = new TransactionService(
                jdbcTemplate, cartService, catalogService, customerService);

        List<TransactionSummaryResponse> transactions = service.getCustomerTransactions(7L);

        assertEquals(List.of(expected), transactions);
        verify(customerService).requireCustomer(7L);
    }

    @Test
    void checkoutDoesNotCreateTransactionWhenStockIsInsufficient() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        CartService cartService = mock(CartService.class);
        CatalogService catalogService = mock(CatalogService.class);
        CustomerService customerService = mock(CustomerService.class);
        ProductResponse product = new ProductResponse(
                10L, 2L, "Toko", "Kopi", null, new BigDecimal("25000.00"),
                1, null, null);
        when(cartService.lockItemsForCheckout(7L))
                .thenReturn(List.of(new CheckoutItem(product, 2)));
        TransactionService service = new TransactionService(
                jdbcTemplate, cartService, catalogService, customerService);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.checkout(7L));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(catalogService, never()).decreaseStock(product.id(), 2);
        verify(jdbcTemplate, never()).update(
                org.mockito.ArgumentMatchers.any(org.springframework.jdbc.core.PreparedStatementCreator.class),
                org.mockito.ArgumentMatchers.any(org.springframework.jdbc.support.KeyHolder.class));
        verify(cartService, never()).clearAfterCheckout(7L);
    }
}
