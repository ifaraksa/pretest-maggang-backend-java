package com.pretest.shop.api;

import java.net.URI;
import java.util.List;

import com.pretest.shop.api.ApiModels.TransactionDetailResponse;
import com.pretest.shop.api.ApiModels.TransactionSummaryResponse;
import com.pretest.shop.transaction.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/customers/{customerId}/checkout")
    public ResponseEntity<TransactionDetailResponse> checkout(@PathVariable long customerId) {
        TransactionDetailResponse transaction = transactionService.checkout(customerId);
        return ResponseEntity.created(URI.create("/api/transactions/" + transaction.id())).body(transaction);
    }

    @GetMapping("/customers/{customerId}/transactions")
    public List<TransactionSummaryResponse> getCustomerTransactions(@PathVariable long customerId) {
        return transactionService.getCustomerTransactions(customerId);
    }

    @GetMapping("/transactions/{transactionId}")
    public TransactionDetailResponse getTransaction(@PathVariable long transactionId) {
        return transactionService.getTransaction(transactionId);
    }
}
