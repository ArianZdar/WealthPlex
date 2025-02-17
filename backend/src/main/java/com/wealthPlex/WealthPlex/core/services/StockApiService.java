package com.wealthPlex.WealthPlex.core.services;

import org.json.JSONArray;
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


    public JSONObject getBestMatchSeeach(String keyword) {
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
        JSONObject data = getBestMatchSeeach(symbol);
        List<Object>  matches = data.getJSONArray("bestMatches").toList();
        if (matches.isEmpty()) return List.of();
        return matches.stream().map(match -> ((Map<String, Object>) match).get("1. symbol").toString()).toList();
        }


    public Map<String, Object> getStockInfo(String symbol) {
        JSONObject data = getStockInformation(symbol);
        Map<String, Object> quoteMap = data.toMap();
        quoteMap = (Map<String, Object>) quoteMap.get("Global Quote");
        if (quoteMap.isEmpty()) return null;
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
