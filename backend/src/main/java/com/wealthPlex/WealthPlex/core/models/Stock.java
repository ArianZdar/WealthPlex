package com.wealthPlex.WealthPlex.core.models;


import lombok.Data;

@Data
public class Stock {
    private String symbol;
    private double price;
    private int amount;
    private double rating;
    private String description;
}
