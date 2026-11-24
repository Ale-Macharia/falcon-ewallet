package com.falcon.ewalet.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SafePlanRequest {
    private String accountNumber;
    private BigDecimal totalAmount;
    private BigDecimal weekdayAmount;
    private BigDecimal weekendAmount;
    private Integer totalDays;
    private Integer weekdayCount;
    private Integer weekendCount;
}