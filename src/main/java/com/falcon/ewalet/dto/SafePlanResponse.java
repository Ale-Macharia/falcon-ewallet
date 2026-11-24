package com.falcon.ewalet.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SafePlanResponse {
    private Long planId;
    private String accountNumber;
    private BigDecimal totalLocked;
    private BigDecimal plannedToSpend;
    private BigDecimal leftoverToReturn;
    private BigDecimal weekdayAmount;
    private BigDecimal weekendAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String message;
}