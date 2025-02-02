package com.wealthPlex.WealthPlex.core.models;


import lombok.Data;

@Data
public class WatchedStock {
    private String symbol;
    private double currentPrice;
    private double change;
    private String changePercent;
}
