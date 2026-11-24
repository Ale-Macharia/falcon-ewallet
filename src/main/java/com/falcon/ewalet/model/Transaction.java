package com.falcon.ewalet.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //one wallet can receive cash from many accounts
    @ManyToOne
    @JoinColumn(name = "from_wallet_id")
    private Wallet fromWallet;


    // one wallet can do many transactions eg, one account can send money to many accounts
    @ManyToOne
    @JoinColumn(name = "to_wallet_id")
    private Wallet toWallet;

    //how much money can a single account transact at once
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    //type of transaction :-: withdraw - outside account, like mpesa
    // deposit - sending to outside acount
    // transfer - wallet to wallet
    @Column(nullable = false, length = 20)
    private String type;

    //Did the transaction happen, it either, Failed, Succeeded or is pending
    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false, unique = true, length = 50)
    private String reference; //like the reference code

    //20 sent to alex, "78 transfered to Eugine," " Received 150 from jane"
    private String description;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreation(){
        this.createdAt = LocalDateTime.now();

        if (this.status == null){
            this.status = "SUCCESS";
        }
    }

}
