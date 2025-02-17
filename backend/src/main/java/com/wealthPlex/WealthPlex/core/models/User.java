package com.wealthPlex.WealthPlex.core.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class User extends DocumentData {
    String username;
    String password;
    boolean isLongTermInvestor;
    String investmentType;
    List<Stock> stocks = new ArrayList<>();
    List<WatchedStock> watchlist = new ArrayList<>();
    Double profit;

    
    public User() {
        this.isLongTermInvestor = false;
        this.investmentType = "short";
    }

    public void setLongTermInvestor(Boolean longTermInvestor) {
        this.isLongTermInvestor = longTermInvestor;
        this.investmentType = longTermInvestor ? "long term investor" : "short term investor"; 
    }
    
    public String getInvestmentType() {
        return investmentType;
    }
}
