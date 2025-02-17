package com.wealthPlex.WealthPlex.core.models;


import lombok.Data;

@Data
public class WatchedStock {
    private String symbol;
    private double currentPrice;
    private double change;
    private String changePercent;
    private double openPrice;
    private double highPrice;
    private double lowPrice;
    private double closePrice;
    private long volume;
    private String lastUpdated;

}
