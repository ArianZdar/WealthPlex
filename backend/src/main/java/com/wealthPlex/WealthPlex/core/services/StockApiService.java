package com.wealthPlex.WealthPlex.core.services;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StockApiService {

    String alphaVantageAPIkey = System.getenv("ALPHA_VANTAGE_API_KEY");

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


    public JSONObject getBestMatchSearch(String keyword) {
        String url = "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords="+keyword+"&apikey=" + alphaVantageAPIkey;
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

    public List<String> getMatches(String symbol) {
        JSONObject data = getBestMatchSearch(symbol);
    
        System.out.println("API Response: " + data.toString());
    
        if (!data.has("bestMatches")) {
            System.err.println("Error: 'bestMatches' not found in API response.");
            return List.of();
        }
    
        List<Object> matches = data.getJSONArray("bestMatches").toList();
    
        if (matches.isEmpty()) {
            return List.of();
        }
    
        return matches.stream()
                      .map(match -> ((Map<String, Object>) match).get("1. symbol").toString())
                      .toList();
    }
    


    public Map<String, Object> getStockInfo(String symbol) {
        JSONObject data = getStockInformation(symbol);

        if (!data.has("Global Quote")) {
        return Map.of("error", "Stock data not found for symbol: " + symbol);
    }

    Map<String, Object> quoteMap = data.getJSONObject("Global Quote").toMap();

    if (quoteMap.isEmpty()) {
        return Map.of("error", "No stock data available.");
    }


    Map<String, Object> stockInfo = new HashMap<>();
    stockInfo.put("openPrice", quoteMap.get("02. open"));
    stockInfo.put("closePrice", quoteMap.get("08. previous close"));
    stockInfo.put("highPrice", quoteMap.get("03. high"));
    stockInfo.put("lowPrice", quoteMap.get("04. low"));
    stockInfo.put("volume", quoteMap.get("06. volume"));
    stockInfo.put("changePercent", quoteMap.get("10. change percent"));
    stockInfo.put("change", quoteMap.get("09. change"));
    stockInfo.put("currentPrice", quoteMap.get("05. price"));
    stockInfo.put("symbol", quoteMap.get("01. symbol"));

    return stockInfo;
}





}
