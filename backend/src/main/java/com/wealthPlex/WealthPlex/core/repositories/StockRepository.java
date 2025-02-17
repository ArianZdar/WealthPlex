package com.wealthPlex.WealthPlex.core.repositories;

import com.google.cloud.firestore.DocumentReference;
import com.wealthPlex.WealthPlex.core.models.Stock;
import com.wealthPlex.WealthPlex.firestore.model.FirestoreImplementation;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class StockRepository {

    private final FirestoreImplementation firestoreImplementation;

    public StockRepository(FirestoreImplementation firestoreImplementation) {
        this.firestoreImplementation = firestoreImplementation;
    }

    public void saveStock(Stock stock) {
        Map<String, Object> stockData = new HashMap<>();
        stockData.put("symbol", stock.getSymbol());
        stockData.put("price", stock.getPrice());
        stockData.put("amount", stock.getAmount());
        stockData.put("rating", stock.getRating());
        stockData.put("description", stock.getDescription());

        firestoreImplementation.addDocumentToCollectionWithId("stocks", stockData, stock.getSymbol());
    }

    public Stock getStock(String symbol) {
        return firestoreImplementation.getDocument("stocks", symbol).toObject(Stock.class);
    }

    public void deleteStock(String symbol) {
        firestoreImplementation.deleteDocument("stocks", symbol);
    }
}
