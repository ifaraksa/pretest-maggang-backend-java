package com.pretest.shop.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class ApiModels {

    private ApiModels() {
    }

    public record CreateStoreRequest(
            @NotBlank @Size(max = 150) String nama,
            String deskripsi,
            @Size(max = 100) String kota) {
    }

    public record StoreResponse(
            Long id,
            String nama,
            String deskripsi,
            String kota,
            LocalDateTime createdAt) {
    }

    public record CreateProductRequest(
            @NotBlank @Size(max = 200) String nama,
            String deskripsi,
            @NotNull @Min(0) BigDecimal harga,
            @NotNull @Min(0) Integer stok) {
    }

    public record ProductResponse(
            Long id,
            Long tokoId,
            String tokoNama,
            String nama,
            String deskripsi,
            BigDecimal harga,
            Integer stok,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
    }

    public record CreateCustomerRequest(
            @NotBlank @Size(max = 150) String nama,
            @NotBlank @Email @Size(max = 150) String email,
            @Size(max = 20) String noTelepon) {
    }

    public record CustomerResponse(
            Long id,
            String nama,
            String email,
            String noTelepon,
            LocalDateTime createdAt) {
    }

    public record AddCartItemRequest(
            @NotNull @Min(1) Long barangId,
            @NotNull @Min(1) Integer jumlah) {
    }

    public record CartLineResponse(
            Long barangId,
            String namaBarang,
            BigDecimal harga,
            Integer stok,
            Integer jumlah,
            BigDecimal subtotal) {
    }

    public record CartResponse(
            Long pelangganId,
            List<CartLineResponse> items,
            BigDecimal totalHarga) {
    }

    public record TransactionSummaryResponse(
            Long id,
            String kodeTransaksi,
            BigDecimal totalHarga,
            String status,
            LocalDateTime createdAt) {
    }

    public record TransactionItemResponse(
            Long barangId,
            String namaBarang,
            BigDecimal hargaSatuan,
            Integer jumlah,
            BigDecimal subtotal) {
    }

    public record TransactionDetailResponse(
            Long id,
            String kodeTransaksi,
            Long pelangganId,
            BigDecimal totalHarga,
            String status,
            LocalDateTime createdAt,
            List<TransactionItemResponse> items) {
    }
}
