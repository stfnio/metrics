package io.stfn.statistics.controllers;

import io.stfn.statistics.repositories.TransactionsRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionsController {
    private static final long MILLIS_IN_MINUTE = 60_000L;

    private final TransactionsRepository transactionsRepository;

    @Autowired
    public TransactionsController(TransactionsRepository transactionsRepository) {
        this.transactionsRepository = transactionsRepository;
    }

    @PostMapping("/transactions")
    public ResponseEntity createTransaction(@RequestBody Transaction transaction) {
        if (System.currentTimeMillis() - transaction.timestamp > MILLIS_IN_MINUTE) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        long transactionSeconds = transaction.timestamp / 1000L;
        transactionsRepository.storeTransaction(transactionSeconds, transaction.amount);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    static class Transaction {
        private double amount;
        private long timestamp;
    }
}
