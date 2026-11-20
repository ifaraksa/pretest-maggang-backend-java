package com.pretest.shop.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import com.pretest.shop.api.ApiModels.ProductResponse;
import com.pretest.shop.cart.CartService;
import com.pretest.shop.cart.CartService.CheckoutItem;
import com.pretest.shop.catalog.CatalogService;
import com.pretest.shop.customer.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;

class TransactionServiceTest {

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
