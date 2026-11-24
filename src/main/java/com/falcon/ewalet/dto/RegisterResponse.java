package com.falcon.ewalet.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RegisterResponse {
    private Long customerId;
    private String fullName;
    private String phoneNumber;
    private String accountNumber;
    private BigDecimal balance;
    private String message;

}
