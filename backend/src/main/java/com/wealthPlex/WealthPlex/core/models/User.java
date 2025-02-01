package com.wealthPlex.WealthPlex.core.models;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class User extends DocumentData {
    String username;
    String password;
    boolean isLongTermInvestor;
    List<Stock> stocks = new ArrayList<>();
}
