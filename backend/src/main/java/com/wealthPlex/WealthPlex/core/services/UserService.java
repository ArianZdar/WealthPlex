package com.wealthPlex.WealthPlex.core.services;


import com.wealthPlex.WealthPlex.core.models.Stock;
import com.wealthPlex.WealthPlex.core.models.User;
import com.wealthPlex.WealthPlex.core.models.WatchedStock;
import com.wealthPlex.WealthPlex.core.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockApiService stockApiService;

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
        return user.getStocks().stream().filter(stock -> stock.getAmount()!=0).map(stock -> userRepository.getStockAsMap(stock)).toList();
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
        user.setProfit(0D);
        user.setWatchlist(new ArrayList<>());
        user.setStocks(new ArrayList<>());
        user.setLongTermInvestor(false);

        userRepository.saveDocumentWithId(username,user);
        return userRepository.getAsMap(user);
    }


    public double getPortfolioValue(String username) {
        User user = (User) userRepository.getDocumentById(username);

        return user.getStocks().stream().
                mapToDouble(stock -> {
                    double stockPrice;
                    Optional<WatchedStock> optWatchedStock = user.getWatchlist().stream().filter(watchedStock -> watchedStock.getSymbol().equals(stock.getSymbol())).findFirst();
                    if (optWatchedStock.isPresent())
                    {
                        stockPrice =optWatchedStock.get().getCurrentPrice();
                    } else {
                        try {
                            stockPrice = Double.parseDouble(stockApiService.getStockInfo(stock.getSymbol()).get("currentPrice").toString());
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    return stockPrice*stock.getAmount();
                }).
                sum();
    }

    public double getAmountPaid(String username) {
        User user = (User) userRepository.getDocumentById(username);
        return user.getStocks().stream().
                mapToDouble(stock -> stock.getAmount()*stock.getAmount()).
                sum();

    }

    public List<Map<String,Object>> addStockToWatchlist(String username, String symbol) throws FileNotFoundException {
        User user = (User) userRepository.getDocumentById(username);
        if (user.getWatchlist().stream().map(WatchedStock::getSymbol).toList().contains(symbol)) {
            return user.getWatchlist().stream().map( stock -> userRepository.getWatchedStockAsMap(stock)).toList();
        }
        WatchedStock watchedStock =  new WatchedStock();
        watchedStock.setSymbol(symbol);
        Map<String,Object> info;
        try {
            info = stockApiService.getStockInfo(symbol);
        } catch (FileNotFoundException e) {
            return user.getWatchlist().stream().map( stock -> userRepository.getWatchedStockAsMap(stock)).toList();
        }
        watchedStock = userRepository.getWatchedStockFromMap(info);
        List<WatchedStock> watchedStocks = new ArrayList<>();
        user.getWatchlist().stream().forEach(watchedStocks::add);
        watchedStocks.add(watchedStock);
        user.setWatchlist(watchedStocks);
        userRepository.saveDocumentWithId(username,user);
        return user.getWatchlist().stream().map( stock -> userRepository.getWatchedStockAsMap(stock)).toList();
    }

    public List<Map<String,Object>> refreshWatchlistValues(String username) throws FileNotFoundException {
        User user = (User) userRepository.getDocumentById(username);
        List<WatchedStock> watchlist = user.getWatchlist().stream().map(stock -> {
                    String symbol = stock.getSymbol();
                    Map<String, Object> info = null;
                    try {
                        info = stockApiService.getStockInfo(symbol);
                    } catch (FileNotFoundException e) {
                        return null;
                    }
                    WatchedStock watchedStock = userRepository.getWatchedStockFromMap(info);
                    return watchedStock;
                })
                .toList();
        if (watchlist.stream().anyMatch(Objects::isNull)) throw new FileNotFoundException("Stock not found");
        user.setWatchlist(watchlist);
        userRepository.saveDocumentWithId(username,user);
        return user.getWatchlist().stream().map( stock -> userRepository.getWatchedStockAsMap(stock)).toList();
    }

    public List<Map<String,Object>> getUserWatchlist(String username) {
        User user = (User) userRepository.getDocumentById(username);
        return user.getWatchlist().stream().map( stock -> userRepository.getWatchedStockAsMap(stock)).toList();
    }

    public List<Map<String,Object>> removeStockFromUser(String username, String stockSymbol) {
        User user = (User) userRepository.getDocumentById(username);
        Stock stockToRemove = getStockFromUser(username,stockSymbol);
        List<Stock> stocks = user.getStocks().stream().toList();
        stocks.remove(stockToRemove);
        user.setStocks(stocks);
        userRepository.saveDocumentWithId(username,user);
        return user.getStocks().stream().map(stock -> userRepository.getStockAsMap(stock)).toList();
    }

    public  List<Map<String,Object>> removeStockFromWatchlist(String username, String stockSymbol) {
        User user = (User) userRepository.getDocumentById(username);
        WatchedStock stockToRemove =  user.getWatchlist().stream().filter(stock -> stock.getSymbol().equals(stockSymbol)).findFirst().orElse(null);
        List<WatchedStock> watchedStocks = new ArrayList<>();
        user.getWatchlist().forEach(watchedStocks::add);
        watchedStocks.remove(stockToRemove);
        user.setWatchlist(watchedStocks);
        userRepository.saveDocumentWithId(username,user);
        return user.getWatchlist().stream().map( stock -> userRepository.getWatchedStockAsMap(stock)).toList();
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
        List<Stock> stocks = new ArrayList<>(user.getStocks());
        boolean hasStock = stocks.stream().anyMatch(stock -> stock.getSymbol().equals(symbol));
        if (!hasStock) {
            Stock stock = new Stock();
            stock.setSymbol(symbol);
            stock.setPrice(price);
            stock.setAmount(amount);
            stocks.add(stock);
        } else {
            Stock addedStock = stocks.stream().filter(stock -> stock.getSymbol().equals(symbol)).findFirst().get();
            double totalPrice = addedStock.getPrice() * addedStock.getAmount();
            totalPrice += price*amount;
            int totalAmount = addedStock.getAmount() + amount;
            double averagePrice = totalPrice / totalAmount;
            averagePrice = Math.round(averagePrice * Math.pow(10, 4)) / Math.pow(10, 4);
            addedStock.setPrice(Math.round(averagePrice));
            addedStock.setAmount(totalAmount);
        }
        user.setStocks(stocks);
        userRepository.saveDocumentWithId(username,user);
        return userRepository.getAsMap(user);
    }

    public double getProfitOnStock(String username, String stockSymbol) throws FileNotFoundException {
        User user = (User) userRepository.getDocumentById(username);
        List<Stock> stocks = user.getStocks();
        Double stockPrice = Double.parseDouble(stockApiService.getStockInfo(stockSymbol).get("currentPrice").toString());
        Stock ownedStock = stocks.stream().filter(stock -> stock.getSymbol().equals(stockSymbol)).findFirst().get();
        return (ownedStock.getAmount()*(stockPrice-ownedStock.getPrice()));

    }

    public double sellStock(String username, String symbol, int amount) throws IllegalArgumentException, FileNotFoundException {
        User user = (User) userRepository.getDocumentById(username);
        Stock stock = getStockFromUser(username, symbol);
        Double stockPrice = Double.parseDouble(stockApiService.getStockInfo(symbol).get("currentPrice").toString());
        double profit;
        if (stock.getAmount() < amount) {
            throw new IllegalArgumentException("Not enough stocks!");
        } else {
            stock.setAmount(stock.getAmount() - amount);
            profit = stock.getPrice() * stockPrice;
        }
        user.setProfit(user.getProfit() + profit);
        List<Stock> newStocks = new ArrayList<>(user.getStocks().stream().filter(userStock -> (!userStock.getSymbol().equals(symbol))).toList());
        if (stock.getAmount() != 0) {
            newStocks.add(stock);
        }
        user.setStocks(newStocks);
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
