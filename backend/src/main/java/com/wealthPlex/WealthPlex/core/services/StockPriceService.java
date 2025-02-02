package com.wealthPlex.WealthPlex.core.services;

import com.wealthPlex.WealthPlex.core.models.WatchedStock;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class StockPriceService {

    String alphaVantageAPIkey = System.getenv("alphaVantageAPIkey");


    public JSONObject getStockInformation(String symbol) {

        String url = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" + symbol + "&apikey="+alphaVantageAPIkey;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        String body = "";
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            body = response.body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        JSONObject data = new JSONObject(body);
        return data;
    }


    public Map<String, Object> getStockInfo(String symbol) {
        JSONObject data = getStockInformation(symbol);
        Map<String, Object> quoteMap = data.toMap();
        quoteMap = (Map<String, Object>) quoteMap.get("Global Quote");
        double price = Double.parseDouble(quoteMap.get("05. price").toString());
        String change = (quoteMap.get("09. change").toString());
        String changePercent = (quoteMap.get("10. change percent").toString());
        Map<String, Object> stockInfo = new HashMap<>();
        stockInfo.put("price", price);
        stockInfo.put("change", change);
        stockInfo.put("changePercent", changePercent);
        return stockInfo;
    }





}
