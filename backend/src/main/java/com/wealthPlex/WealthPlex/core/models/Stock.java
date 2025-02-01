package com.wealthPlex.WealthPlex.core.models;


import lombok.Data;

@Data
public class Stock {
    private String symbol;
    private double price;
    private double amount;
}
