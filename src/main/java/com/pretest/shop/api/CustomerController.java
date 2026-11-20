package com.pretest.shop.api;

import java.net.URI;

import com.pretest.shop.api.ApiModels.AddCartItemRequest;
import com.pretest.shop.api.ApiModels.CartResponse;
import com.pretest.shop.api.ApiModels.CreateCustomerRequest;
import com.pretest.shop.api.ApiModels.CustomerResponse;
import com.pretest.shop.cart.CartService;
import com.pretest.shop.customer.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CustomerController {

    private final CustomerService customerService;
    private final CartService cartService;

    public CustomerController(CustomerService customerService, CartService cartService) {
        this.customerService = customerService;
        this.cartService = cartService;
    }

    @PostMapping("/customers")
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        CustomerResponse customer = customerService.createCustomer(request);
        return ResponseEntity.created(URI.create("/api/customers/" + customer.id())).body(customer);
    }

    @GetMapping("/customers/{customerId}/cart")
    public CartResponse getCart(@PathVariable long customerId) {
        return cartService.getCart(customerId);
    }

    @PostMapping("/customers/{customerId}/cart/items")
    public CartResponse addCartItem(
            @PathVariable long customerId,
            @Valid @RequestBody AddCartItemRequest request) {
        return cartService.addItem(customerId, request.barangId(), request.jumlah());
    }

    @PutMapping("/customers/{customerId}/cart/items/{productId}")
    public CartResponse setCartItemQuantity(
            @PathVariable long customerId,
            @PathVariable long productId,
            @Valid @RequestBody QuantityRequest request) {
        return cartService.setItemQuantity(customerId, productId, request.jumlah());
    }

    @DeleteMapping("/customers/{customerId}/cart/items/{productId}")
    public ResponseEntity<Void> removeCartItem(
            @PathVariable long customerId,
            @PathVariable long productId) {
        cartService.removeItem(customerId, productId);
        return ResponseEntity.noContent().build();
    }

    public record QuantityRequest(@jakarta.validation.constraints.NotNull
                                  @jakarta.validation.constraints.Min(1) Integer jumlah) {
    }
}
