# Falcon eWallet

A secure digital wallet application built with Java and Spring Boot. It allows users to create accounts, deposit, withdraw, transfer money, and manage savings through a structured savings feature called MySafe.

The project was developed as a practical implementation of wallet and fintech concepts, similar to local mobile money systems and digital payment platforms.

## Features

### User Management
- User registration with full name, phone number, email, national ID, and PIN
- Secure authentication using BCrypt password hashing
- Role-based access: `CUSTOMER` and `ADMIN`
- Customers authenticate with a 4-digit numeric PIN, similar to M-Pesa
- Administrators authenticate with a password

### Wallet Operations
- Create a wallet automatically on registration with a unique account number
- Deposit funds
- Withdraw funds with balance validation
- Transfer funds between accounts with validation for insufficient balance and self-transfer
- View wallet balance and transaction history
- Unique transaction reference for every operation

### M-Pesa STK Push (Simulated)
- Simulates the Daraja STK Push deposit flow
- User enters amount and M-Pesa phone number
- System simulates sending a payment prompt to the phone
- Designed to be replaced with the real Safaricom Daraja API in production

### MySafe - Structured Savings
MySafe is an optional savings feature built on top of the wallet.

A user locks a total amount for a defined period and receives a fixed daily allowance instead of having access to the full amount at once.

Example:
- Total locked: KES 30,000 for 30 days
- Weekday allowance: KES 800 x 22 days = KES 17,600
- Weekend allowance: KES 1,500 x 8 days = KES 12,000
- Total planned: KES 29,600
- Leftover: KES 400 returned at the end of the period

Rules:
- Locked amount is deducted from the wallet balance immediately
- Daily allowance is credited back to the wallet on claim
- Weekend and weekday amounts can differ
- Any unallocated leftover is returned when the plan completes
- Plans have statuses: `LOCKED` and `COMPLETED`

### Admin Dashboard
- Total registered users
- Total wallet balances across the system
- Total amount currently locked in MySafe plans
- Total number of transactions
- List of all users
- Full transaction ledger for audit purposes

## Technology Stack

- **Backend:** Java 21, Spring Boot 4.1.2
- **Persistence:** Spring Data JPA, Hibernate
- **Database:** H2 (file-based, persists between restarts)
- **Security:** Spring Security Crypto (BCrypt)
- **Frontend:** HTML, CSS, JavaScript (no framework)
- **Build Tool:** Maven

## Project Structure

src/main/java/com/falcon/ewalet/
├── EWaletApplication.java
├── DataSeeder.java
├── controller/
│   ├── WalletController.java
│   └── AdminController.java
├── service/
│   └── WalletService.java
├── repository/
│   ├── CustomerRepository.java
│   ├── WalletRepository.java
│   ├── TransactionRepository.java
│   └── SavingsPlanRepository.java
├── model/
│   ├── Customer.java
│   ├── Wallet.java
│   ├── Transaction.java
│   └── SavingsPlan.java
└── dto/
    ├── RegisterRequest.java
    ├── RegisterResponse.java
    ├── SafePlanRequest.java
    └── SafePlanResponse.javasrc/main/resources/
├── application.properties
└── static/index.html



## Getting Started

### Prerequisites
- Java 21 or higher
- Maven
- IntelliJ IDEA or any Java IDE (optional)

Run the Application

bash
git clone https://github.com/Ale-Macharia/falcon-ewallet.git
cd falcon-ewallet
./mvnw spring-boot:run

Or run EWaletApplication.java from your IDE.
Open in browser:
http://localhost:8080

No external database setup is required. The application uses a file-based H2 database stored in the data/ directory.

Future Improvements:

Integration with Safaricom Daraja API for real M-Pesa paymentsScheduled automatic daily payouts for MySafe plansSMS / email notifications for transactionsSpending analytics and chartsJWT-based authentication

Author: Alex Macharia Kariuki
Nairobi, Kenya
