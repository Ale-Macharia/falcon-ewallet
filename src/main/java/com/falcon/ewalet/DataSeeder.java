package com.falcon.ewalet;

import com.falcon.ewalet.service.WalletService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {
    private final WalletService walletService;
    public DataSeeder(WalletService walletService) { this.walletService = walletService; }
    @Override
    public void run(String... args) {
        walletService.seedDemoData();
    }
}