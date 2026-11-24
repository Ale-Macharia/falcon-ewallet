package com.falcon.ewalet.repository;

import com.falcon.ewalet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByAccountNumber(String accountNumber);
    Optional<Wallet> findByCustomerId(Long customerId);
}