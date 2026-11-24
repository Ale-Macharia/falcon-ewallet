package com.falcon.ewalet.controller;

import com.falcon.ewalet.model.Customer;
import com.falcon.ewalet.model.Transaction;
import com.falcon.ewalet.repository.TransactionRepository;
import com.falcon.ewalet.service.WalletService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final WalletService walletService;
    private final TransactionRepository transactionRepository;

    public AdminController(WalletService walletService, TransactionRepository transactionRepository) {
        this.walletService = walletService;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/users")
    public List<Customer> users() {
        return walletService.getAllCustomers();
    }

    @GetMapping("/ledger")
    public List<Transaction> ledger() {
        return transactionRepository.findAll();
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        Map<String, Object> map = new HashMap<>();
        map.put("totalUsers", walletService.getAllCustomers().size());
        map.put("totalWalletBalances", walletService.getTotalWalletBalances());
        map.put("totalLockedInSafe", walletService.getTotalLockedInSafe());
        map.put("totalTransactions", walletService.getTotalTransactions());
        return map;
    }
}