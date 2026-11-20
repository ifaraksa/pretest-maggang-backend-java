package com.pretest.shop.api;

import java.net.URI;
import java.util.List;

import com.pretest.shop.api.ApiModels.CreateProductRequest;
import com.pretest.shop.api.ApiModels.CreateStoreRequest;
import com.pretest.shop.api.ApiModels.ProductResponse;
import com.pretest.shop.api.ApiModels.StoreResponse;
import com.pretest.shop.catalog.CatalogService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @PostMapping("/api/stores")
    public ResponseEntity<StoreResponse> createStore(@Valid @RequestBody CreateStoreRequest request) {
        StoreResponse store = catalogService.createStore(request);
        return ResponseEntity.created(URI.create("/api/stores/" + store.id())).body(store);
    }

    @PostMapping("/api/stores/{storeId}/products")
    public ResponseEntity<ProductResponse> createProduct(
            @PathVariable long storeId,
            @Valid @RequestBody CreateProductRequest request) {
        ProductResponse product = catalogService.createProduct(storeId, request);
        return ResponseEntity.created(URI.create("/api/products/" + product.id())).body(product);
    }

    @GetMapping("/api/products")
    public List<ProductResponse> searchProducts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long tokoId) {
        return catalogService.searchProducts(q, tokoId);
    }

    @GetMapping("/api/products/{productId}")
    public ProductResponse getProduct(@PathVariable long productId) {
        return catalogService.getProduct(productId);
    }

    @PutMapping("/api/products/{productId}")
    public ProductResponse updateProduct(
            @PathVariable long productId,
            @Valid @RequestBody CreateProductRequest request) {
        return catalogService.updateProduct(productId, request);
    }
}
