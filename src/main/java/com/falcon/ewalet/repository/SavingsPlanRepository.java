package com.falcon.ewalet.repository;

import com.falcon.ewalet.model.SavingsPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SavingsPlanRepository extends JpaRepository<SavingsPlan, Long> {
    List<SavingsPlan> findByWalletId(Long walletId);
}