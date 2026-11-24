package com.falcon.ewalet.repository;

import com.falcon.ewalet.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFromWalletIdOrToWalletId(Long fromWalletId, Long toWalletId);
    Optional<Transaction> findByReference(String reference);
}