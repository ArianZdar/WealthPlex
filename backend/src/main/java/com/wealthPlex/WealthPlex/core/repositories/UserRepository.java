package com.wealthPlex.WealthPlex.core.repositories;

import com.google.cloud.firestore.DocumentReference;
import com.wealthPlex.WealthPlex.core.models.DocumentData;
import com.wealthPlex.WealthPlex.core.models.Stock;
import com.wealthPlex.WealthPlex.core.models.User;
import com.wealthPlex.WealthPlex.core.models.WatchedStock;
import com.wealthPlex.WealthPlex.firestore.model.FirestoreImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepository extends DocumentRepository {

    @Autowired
    private final FirestoreImplementation firestoreImplementation;

    public UserRepository(FirestoreImplementation firestoreImplementation) {
        this.collectionName = "users";
        this.firestoreImplementation = firestoreImplementation;
    }


    @Override
    public Map<String, Object> getAsMap(DocumentData data) {
        User user = (User) data;
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());
        userMap.put("password", user.getPassword());
        userMap.put("isLongTermInvestor", user.isLongTermInvestor());
        userMap.put("profit",user.getProfit());
        List<Map<String,Object>> stocks = user.getStocks().stream()
                .map(this::getStockAsMap).toList();
        List<Map<String,Object>> watchlist = user.getWatchlist().stream()
                        .map(this::getWatchedStockAsMap).toList();
        userMap.put("watchlist", watchlist);
        userMap.put("stocks", stocks);

        return userMap;
    }

    @Override
    public DocumentData getFromMap(Map<String, Object> map) {
        User user = new User();
        user.setId(map.get("id").toString());
        user.setProfit(Double.parseDouble(map.get("profit").toString()));
        user.setUsername(map.get("username").toString());
        user.setPassword(map.get("password").toString());
        user.setLongTermInvestor((Boolean) map.get("isLongTermInvestor"));
        List<Map<String,Object>> stocks = (List<Map<String, Object>>) map.get("stocks");
        user.setStocks(stocks.stream().map(this::getStockFromMap).toList());
        List<Map<String,Object>> watchlist = (List<Map<String, Object>>) map.get("watchlist");
        user.setWatchlist(watchlist.stream().map(this::getWatchedStockFromMap).toList());
        return user;
    }

    @Override
    public DocumentReference saveDocument(DocumentData data) {
        User user = (User) data;
        Map<String,Object> userMap = getAsMap(data);
        return firestoreImplementation.addDocumentToCollectionWithId(collectionName,userMap,((User) data).getUsername());
    }


    public Map<String, Object> getStockAsMap(Stock stock) {
        Map<String, Object> stockMap = new HashMap<String, Object>();
        stockMap.put("symbol",stock.getSymbol());
        stockMap.put("price",stock.getPrice());
        stockMap.put("amount",stock.getAmount());
        return stockMap;
    }

    public Map<String, Object> getWatchedStockAsMap(WatchedStock watchedStock) {
        Map<String, Object> stockMap = new HashMap<String, Object>();
        stockMap.put("symbol",watchedStock.getSymbol());
        stockMap.put("currentPrice",watchedStock.getCurrentPrice());
        return stockMap;
    }

    public Stock getStockFromMap(Map<String, Object> map) {
        Stock stock = new Stock();
        stock.setSymbol((String) map.get("symbol"));
        stock.setAmount(Integer.parseInt(map.get("amount").toString()));
        stock.setPrice(Double.parseDouble(map.get("price").toString()));
        return stock;
    }

    public WatchedStock getWatchedStockFromMap(Map<String, Object> map) {
        WatchedStock watchedStock = new WatchedStock();
        watchedStock.setSymbol((String) map.get("symbol"));
        watchedStock.setCurrentPrice(Double.parseDouble(map.get("currentPrice").toString()));
        return watchedStock;
    }


}
