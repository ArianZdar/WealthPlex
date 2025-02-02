package com.wealthPlex.WealthPlex.core.services;


import com.wealthPlex.WealthPlex.core.models.Stock;
import com.wealthPlex.WealthPlex.core.models.User;
import com.wealthPlex.WealthPlex.core.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public Map<String, Object> getUserByUsername(String username) throws FileNotFoundException {
        boolean doesNotExist = userRepository.idAvailable(username,userRepository.collectionName);
        if (doesNotExist) {
            throw new FileNotFoundException("User " + username +" does not exist!");
        }
        System.out.println("AAA");
        User user = (User) userRepository.getDocumentById(username);
        return userRepository.getAsMap(user);
    }


    public List<Map<String, Object>> getUserStocks(String username) throws FileNotFoundException {
        boolean doesNotExist = userRepository.idAvailable(username,userRepository.collectionName);
        if (doesNotExist) {
            throw new FileNotFoundException("User " + username +" does not exist!");
        }
        User user = (User) userRepository.getDocumentById(username);
        return user.getStocks().stream().map(stock -> userRepository.getStockAsMap(stock)).toList();
    }

    public Map<String, Object> signUp(String username, String password) {
        boolean doesNotExist = userRepository.idAvailable(username,userRepository.collectionName);
        if (!doesNotExist) {
            throw new IllegalArgumentException("username : " + username +" not available!");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setId(username);

        userRepository.saveDocumentWithId(username,user);
        return userRepository.getAsMap(user);
    }

    public Map<String, Object> login(String username, String password) {
        boolean doesNotExist = userRepository.idAvailable(username,userRepository.collectionName);
        if (doesNotExist) {
            throw new IllegalArgumentException("User " + username +" does not exist!");
        }

        User user = (User) userRepository.getDocumentById(username);
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Wrong password!");
        } else {
            return userRepository.getAsMap(user);
        }
    }


    public Map<String, Object> setInvestorType(String username, boolean isLongTermInvestor) {
        User user = (User) userRepository.getDocumentById(username);
        user.setLongTermInvestor(isLongTermInvestor);
        userRepository.saveDocumentWithId(username,user);
        return userRepository.getAsMap(user);
    }

    public Stock getStockFromUser(String username, String stockSymbol) {
        User user = (User) userRepository.getDocumentById(username);
        List<Stock> stocks = user.getStocks();
        Stock stock = stocks.stream().
                filter((Stock ownedStock ) -> ownedStock.getSymbol().equals(stockSymbol)).
                findFirst().orElse(null);
        return stock;
    }

    public Map<String, Object> buyStock(String username, String symbol, double price, int amount) throws FileNotFoundException {
        User user = (User) userRepository.getFromMap(getUserByUsername(username));
        List<Stock> stocks = user.getStocks();
        boolean hasStock = stocks.stream().anyMatch(stock -> stock.getSymbol().equals(symbol));
        if (!hasStock) {
            Stock stock = new Stock();
            stock.setSymbol(symbol);
            stock.setPrice(price);
            stock.setAmount(amount);
            stocks.add(stock);
        } else {
            Stock stock = getStockFromUser(username, symbol);
            double totalPrice = stock.getPrice() * stock.getAmount();
            totalPrice += price*amount;
            int totalAmount = stock.getAmount() + amount;
            stock.setPrice(totalPrice/totalAmount);
            stock.setAmount(totalAmount);
        }
        userRepository.saveDocumentWithId(username,user);
        return userRepository.getAsMap(user);
    }

    public double sellStock(String username, String symbol, double price, int amount) throws IllegalArgumentException {
        User user = (User) userRepository.getDocumentById(username);
        Stock stock = getStockFromUser(username, symbol);
        double profit;
        if (stock.getAmount() < amount) {
            throw new IllegalArgumentException("Not enough stocks!");
        } else {
            stock.setAmount(stock.getAmount() - amount);
            profit = stock.getPrice() * price;
        }
        user.setProfit(user.getProfit() + profit);
        userRepository.saveDocumentWithId(username,user);
        return profit;
    }

    public Map<String,Object> setStockAmount(String username, String symbol, int newAmount) {
        User user = (User) userRepository.getDocumentById(username);
        Stock stock = getStockFromUser(username, symbol);
        stock.setAmount(newAmount);
        userRepository.saveDocumentWithId(username,user);
        return userRepository.getStockAsMap(stock);
    }

    public double getUserProfit(String username) {
        User user = (User) userRepository.getDocumentById(username);
        return user.getProfit();
    }

}
