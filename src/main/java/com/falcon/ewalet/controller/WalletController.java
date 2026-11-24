package com.falcon.ewalet.controller;

import com.falcon.ewalet.dto.RegisterRequest;
import com.falcon.ewalet.dto.RegisterResponse;
import com.falcon.ewalet.dto.SafePlanRequest;
import com.falcon.ewalet.dto.SafePlanResponse;
import com.falcon.ewalet.model.Customer;
import com.falcon.ewalet.model.SavingsPlan;
import com.falcon.ewalet.model.Transaction;
import com.falcon.ewalet.model.Wallet;
import com.falcon.ewalet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
    private final WalletService walletService;
    public WalletController(WalletService walletService) { this.walletService = walletService; }

    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return walletService.register(request);
    }

    @PostMapping("/login")
    public Customer login(@RequestParam String phoneNumber, @RequestParam String pin) {
        return walletService.login(phoneNumber, pin);
    }

    @GetMapping("/wallet-by-phone")
    public Wallet walletByPhone(@RequestParam String phoneNumber) {
        return walletService.getWalletByPhone(phoneNumber);
    }

    @PostMapping("/stk-push")
    public String stkPush(@RequestParam String accountNumber, @RequestParam BigDecimal amount, @RequestParam String phoneNumber) throws InterruptedException {
        Thread.sleep(2000);
        walletService.deposit(accountNumber, amount);
        return "STK Push sent to " + phoneNumber + ". KES " + amount + " deposited to " + accountNumber;
    }

    @PostMapping("/deposit")
    public Wallet deposit(@RequestParam String accountNumber, @RequestParam BigDecimal amount) {
        return walletService.deposit(accountNumber, amount);
    }

    @PostMapping("/withdraw")
    public Wallet withdraw(@RequestParam String accountNumber, @RequestParam BigDecimal amount) {
        return walletService.withdraw(accountNumber, amount);
    }

    @PostMapping("/transfer")
    public String transfer(@RequestParam String fromAccount, @RequestParam String toAccount, @RequestParam BigDecimal amount) {
        return walletService.transfer(fromAccount, toAccount, amount);
    }

    @GetMapping("/balance")
    public Wallet balance(@RequestParam String accountNumber) {
        return walletService.getBalance(accountNumber);
    }

    @GetMapping("/history")
    public List<Transaction> history(@RequestParam String accountNumber) {
        return walletService.getHistory(accountNumber);
    }

    @PostMapping("/safe/create")
    public SafePlanResponse createSafe(@RequestBody SafePlanRequest request) {
        return walletService.createSafePlan(request);
    }

    @PostMapping("/safe/claim/{planId}")
    public BigDecimal claimDaily(@PathVariable Long planId) {
        return walletService.claimDaily(planId);
    }

    @GetMapping("/safe/my-plans")
    public List<SavingsPlan> myPlans(@RequestParam String accountNumber) {
        return walletService.getMySafePlans(accountNumber);
    }
}