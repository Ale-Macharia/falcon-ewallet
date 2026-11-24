package com.falcon.ewalet.service;

import com.falcon.ewalet.dto.RegisterRequest;
import com.falcon.ewalet.dto.RegisterResponse;
import com.falcon.ewalet.dto.SafePlanRequest;
import com.falcon.ewalet.dto.SafePlanResponse;
import com.falcon.ewalet.model.Customer;
import com.falcon.ewalet.model.SavingsPlan;
import com.falcon.ewalet.model.Transaction;
import com.falcon.ewalet.model.Wallet;
import com.falcon.ewalet.repository.CustomerRepository;
import com.falcon.ewalet.repository.SavingsPlanRepository;
import com.falcon.ewalet.repository.TransactionRepository;
import com.falcon.ewalet.repository.WalletRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class WalletService {
    private final CustomerRepository customerRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final SavingsPlanRepository savingsPlanRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public WalletService(CustomerRepository customerRepository, WalletRepository walletRepository, TransactionRepository transactionRepository, SavingsPlanRepository savingsPlanRepository) {
        this.customerRepository = customerRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.savingsPlanRepository = savingsPlanRepository;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already registered");
        }
        Customer customer = new Customer();
        customer.setFullName(request.getFullName());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setEmail(request.getEmail());
        customer.setNationalId(request.getNationalId());
        customer.setPin(encoder.encode(request.getPin()));
        customer.setRole("CUSTOMER");
        Customer savedCustomer = customerRepository.save(customer);
        Wallet wallet = new Wallet();
        wallet.setCustomer(savedCustomer);
        wallet.setAccountNumber(generateAccountNumber());
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency("KES");
        wallet.setStatus("ACTIVE");
        Wallet savedWallet = walletRepository.save(wallet);
        RegisterResponse response = new RegisterResponse();
        response.setCustomerId(savedCustomer.getId());
        response.setFullName(savedCustomer.getFullName());
        response.setPhoneNumber(savedCustomer.getPhoneNumber());
        response.setAccountNumber(savedWallet.getAccountNumber());
        response.setBalance(savedWallet.getBalance());
        response.setMessage("Account created successfully");
        return response;
    }

    public Customer login(String phoneNumber, String plainPin) {
        Customer customer = customerRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new RuntimeException("User not found"));
        if (!encoder.matches(plainPin, customer.getPin())) {
            throw new RuntimeException("Wrong PIN");
        }
        return customer;
    }

    public Wallet getWalletByPhone(String phoneNumber) {
        Customer customer = customerRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new RuntimeException("User not found"));
        return walletRepository.findByCustomerId(customer.getId()).orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    @Transactional
    public Wallet deposit(String accountNumber, BigDecimal amount) {
        Wallet wallet = walletRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new RuntimeException("Wallet not found"));
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
        Transaction transaction = new Transaction();
        transaction.setToWallet(wallet);
        transaction.setAmount(amount);
        transaction.setType("DEPOSIT");
        transaction.setStatus("SUCCESS");
        transaction.setReference(UUID.randomUUID().toString().substring(0, 12));
        transaction.setDescription("Deposit");
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
        return wallet;
    }

    @Transactional
    public Wallet withdraw(String accountNumber, BigDecimal amount) {
        Wallet wallet = walletRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new RuntimeException("Wallet not found"));
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);
        Transaction transaction = new Transaction();
        transaction.setFromWallet(wallet);
        transaction.setAmount(amount);
        transaction.setType("WITHDRAW");
        transaction.setStatus("SUCCESS");
        transaction.setReference(UUID.randomUUID().toString().substring(0, 12));
        transaction.setDescription("Withdraw");
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
        return wallet;
    }

    @Transactional
    public String transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {

        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new RuntimeException("Cannot transfer to same account");
        }

        Wallet senderWallet = walletRepository.findByAccountNumber(fromAccountNumber).orElseThrow(() -> new RuntimeException("Sender not found"));
        Wallet receiverWallet = walletRepository.findByAccountNumber(toAccountNumber).orElseThrow(() -> new RuntimeException("Receiver not found"));
        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        receiverWallet.setBalance(receiverWallet.getBalance().add(amount));
        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);
        Transaction transaction = new Transaction();
        transaction.setFromWallet(senderWallet);
        transaction.setToWallet(receiverWallet);
        transaction.setAmount(amount);
        transaction.setType("TRANSFER");
        transaction.setStatus("SUCCESS");
        transaction.setReference(UUID.randomUUID().toString().substring(0, 12));
        transaction.setDescription("Transfer to " + toAccountNumber);
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
        return transaction.getReference();
    }

    public Wallet getBalance(String accountNumber) {
        return walletRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    public List<Transaction> getHistory(String accountNumber) {
        Wallet wallet = walletRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new RuntimeException("Wallet not found"));
        return transactionRepository.findByFromWalletIdOrToWalletId(wallet.getId(), wallet.getId());
    }

    @Transactional
    public SafePlanResponse createSafePlan(SafePlanRequest request) {
        Wallet wallet = walletRepository.findByAccountNumber(request.getAccountNumber()).orElseThrow(() -> new RuntimeException("Wallet not found"));
        if (wallet.getBalance().compareTo(request.getTotalAmount()) < 0) {
            throw new RuntimeException("Insufficient balance to lock into Safe");
        }
        BigDecimal weekdayTotal = request.getWeekdayAmount().multiply(BigDecimal.valueOf(request.getWeekdayCount()));
        BigDecimal weekendTotal = request.getWeekendAmount().multiply(BigDecimal.valueOf(request.getWeekendCount()));
        BigDecimal plannedTotal = weekdayTotal.add(weekendTotal);
        if (plannedTotal.compareTo(request.getTotalAmount()) > 0) {
            throw new RuntimeException("Plan exceeds locked amount");
        }
        wallet.setBalance(wallet.getBalance().subtract(request.getTotalAmount()));
        walletRepository.save(wallet);
        SavingsPlan savingsPlan = new SavingsPlan();
        savingsPlan.setWallet(wallet);
        savingsPlan.setTotalAmount(request.getTotalAmount());
        savingsPlan.setRemainingAmount(request.getTotalAmount());
        savingsPlan.setWeekdayAmount(request.getWeekdayAmount());
        savingsPlan.setWeekendAmount(request.getWeekendAmount());
        savingsPlan.setTotalDays(request.getTotalDays());
        savingsPlan.setDaysPaid(0);
        savingsPlan.setStartDate(LocalDate.now());
        savingsPlan.setEndDate(LocalDate.now().plusDays(request.getTotalDays()));
        savingsPlan.setStatus("LOCKED");
        savingsPlan.setCreatedAt(LocalDateTime.now());
        SavingsPlan savedPlan = savingsPlanRepository.save(savingsPlan);
        BigDecimal leftover = request.getTotalAmount().subtract(plannedTotal);
        SafePlanResponse response = new SafePlanResponse();
        response.setPlanId(savedPlan.getId());
        response.setAccountNumber(wallet.getAccountNumber());
        response.setTotalLocked(savedPlan.getTotalAmount());
        response.setPlannedToSpend(plannedTotal);
        response.setLeftoverToReturn(leftover);
        response.setWeekdayAmount(savedPlan.getWeekdayAmount());
        response.setWeekendAmount(savedPlan.getWeekendAmount());
        response.setStartDate(savedPlan.getStartDate());
        response.setEndDate(savedPlan.getEndDate());
        response.setStatus(savedPlan.getStatus());
        response.setMessage("Safe locked. Wallet balance reduced. Leftover " + leftover + " returns at end.");
        return response;
    }

    @Transactional
    public BigDecimal claimDaily(Long planId) {
        SavingsPlan savingsPlan = savingsPlanRepository.findById(planId).orElseThrow(() -> new RuntimeException("Safe plan not found"));
        if (!savingsPlan.getStatus().equals("LOCKED")) {
            throw new RuntimeException("Plan is not active");
        }
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        boolean isWeekend = today == DayOfWeek.SATURDAY || today == DayOfWeek.SUNDAY;
        BigDecimal todayAmount = isWeekend ? savingsPlan.getWeekendAmount() : savingsPlan.getWeekdayAmount();
        if (savingsPlan.getRemainingAmount().compareTo(todayAmount) < 0) {
            todayAmount = savingsPlan.getRemainingAmount();
        }
        Wallet wallet = savingsPlan.getWallet();
        wallet.setBalance(wallet.getBalance().add(todayAmount));
        walletRepository.save(wallet);
        savingsPlan.setRemainingAmount(savingsPlan.getRemainingAmount().subtract(todayAmount));
        savingsPlan.setDaysPaid(savingsPlan.getDaysPaid() + 1);
        if (savingsPlan.getDaysPaid() >= savingsPlan.getTotalDays()) {
            if (savingsPlan.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0) {
                wallet.setBalance(wallet.getBalance().add(savingsPlan.getRemainingAmount()));
                walletRepository.save(wallet);
                savingsPlan.setRemainingAmount(BigDecimal.ZERO);
            }
            savingsPlan.setStatus("COMPLETED");
        }
        savingsPlanRepository.save(savingsPlan);
        Transaction transaction = new Transaction();
        transaction.setToWallet(wallet);
        transaction.setAmount(todayAmount);
        transaction.setType("SAFE_PAYOUT");
        transaction.setStatus("SUCCESS");
        transaction.setReference(UUID.randomUUID().toString().substring(0, 12));
        transaction.setDescription("MySafe daily payout");
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
        return todayAmount;
    }

    public List<SavingsPlan> getMySafePlans(String accountNumber) {
        Wallet wallet = walletRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new RuntimeException("Wallet not found"));
        return savingsPlanRepository.findByWalletId(wallet.getId());
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public BigDecimal getTotalWalletBalances() {
        return walletRepository.findAll().stream().map(Wallet::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalLockedInSafe() {
        return savingsPlanRepository.findAll().stream().filter(plan -> plan.getStatus().equals("LOCKED")).map(SavingsPlan::getRemainingAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long getTotalTransactions() {
        return transactionRepository.count();
    }

    public void seedDemoData() {
        if (!customerRepository.existsByPhoneNumber("0700000001")) {
            createDemoUser("Alex Macharia", "0700000001", "alex@falcon.com", "12345678", "1234", "CUSTOMER", new BigDecimal("45000"));
        }
        if (!customerRepository.existsByPhoneNumber("0700000002")) {
            createDemoUser("Mary Njoki", "0700000002", "mary@falcon.com", "87654321", "1234", "CUSTOMER", new BigDecimal("12000"));
        }
        if (!customerRepository.existsByPhoneNumber("0700000000")) {
            createDemoUser("Admin Falcon", "0700000000", "admin@falcon.com", "00000000", "admin123", "ADMIN", new BigDecimal("0"));
        }
    }

    private void createDemoUser(String fullName, String phoneNumber, String email, String nationalId, String plainPin, String role, BigDecimal initialDeposit) {
        Customer customer = new Customer();
        customer.setFullName(fullName);
        customer.setPhoneNumber(phoneNumber);
        customer.setEmail(email);
        customer.setNationalId(nationalId);
        customer.setPin(encoder.encode(plainPin));
        customer.setRole(role);
        Customer savedCustomer = customerRepository.save(customer);
        Wallet wallet = new Wallet();
        wallet.setCustomer(savedCustomer);
        wallet.setAccountNumber(generateAccountNumber());
        wallet.setBalance(initialDeposit);
        wallet.setCurrency("KES");
        wallet.setStatus("ACTIVE");
        walletRepository.save(wallet);
    }

    private String generateAccountNumber() {
        Random random = new Random();
        String accountNumber;
        do {
            accountNumber = "254" + (1000000 + random.nextInt(9000000));
        } while (walletRepository.findByAccountNumber(accountNumber).isPresent());
        return accountNumber;
    }
}