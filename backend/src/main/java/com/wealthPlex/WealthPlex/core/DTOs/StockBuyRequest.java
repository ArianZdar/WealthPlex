package com.wealthPlex.WealthPlex.core.DTOs;

import lombok.Data;

@Data
public class StockBuyRequest {
    private int amount;
    private double price;
}
