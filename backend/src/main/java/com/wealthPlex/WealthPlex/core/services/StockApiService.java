package com.wealthPlex.WealthPlex.core.services;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
public class StockApiService {

    String finnhubAPIKey = System.getenv("FINNHUB_API_KEY");

    public JSONObject getStockInformation(String symbol) {

        String url = "https://finnhub.io/api/v1/quote/?symbol="+symbol+"&token=" + finnhubAPIKey;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        String body = "";
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            body = response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error fetching stock history: " + e.getMessage());
        }

        JSONObject data = new JSONObject(body);
        return data;

    }


    public JSONObject getBestMatchSearch(String keyword) {
        String url = "https://finnhub.io/api/v1/search?q="+keyword+"&exchange=US&token=" + finnhubAPIKey;
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
    
        List<Object> matches = data.getJSONArray("result").toList();
    
        if (matches.isEmpty()) {
            return List.of();
        }
    
        return matches.stream()
                      .map(match -> ((Map<String, Object>) match).get("symbol").toString())
                      .toList();
    }
    public Map<String, Object> queryStockHistory(String symbol) {
        String url = "https://query1.finance.yahoo.com/v8/finance/chart/" + symbol + "?interval=1d&range=30d";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .header("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; .NET CLR 1.0.3705;)")
                .uri(URI.create(url))
                .GET()
                .build();

        String body = "";
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            body = response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error fetching stock history: " + e.getMessage());
        }

        JSONObject data = new JSONObject(body);
        Map<String, Object> dataMap = data.toMap();
        dataMap = (Map<String, Object>) dataMap.get("chart");
        List<Object> results = (List<Object>) dataMap.get("result");
        Map<String,Object> info = (Map<String, Object>) results.get(0);
        return info;
    }

    public List<Map<String, Object>> getStockHistory(String symbol) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Map<String, Object> query = queryStockHistory(symbol);
        List<Integer> timeStamps = (List<Integer>) query.get("timestamp");
        HashMap<String, Object> indicators = (HashMap<String, Object>) query.get("indicators");
        List<HashMap<String, List<Object>>> quotes = (List<HashMap<String, List<Object>>>) indicators.get("quote");
        return IntStream.range(0,timeStamps.size()).mapToObj((num -> {
            Map<String, Object> stockHistory = new HashMap<>();
            stockHistory.put("price", Float.parseFloat(quotes.get(0).get("close").get(num).toString()));
            long timeStamp = Long.parseLong(timeStamps.get(num).toString());
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timeStamp), ZoneId.of("UTC"));
            String formattedDate = dateTime.format(formatter);
            stockHistory.put("date", formattedDate);
            return stockHistory;
        })).toList();

    }

    
    


    public Map<String, Object> getStockInfo(String symbol) {

    JSONObject data = getStockInformation(symbol);
    Map<String, Object> quoteMap = data.toMap();
    Map<String, Object> altMap = (Map<String, Object>) queryStockHistory(symbol).get("meta");

    if (quoteMap.isEmpty()) {
        return Map.of("error", "No stock data available.");
    }
    Map<String, Object> stockInfo = new HashMap<>();
    stockInfo.put("openPrice", quoteMap.get("o"));
    stockInfo.put("closePrice", quoteMap.get("pc"));
    stockInfo.put("highPrice", quoteMap.get("h"));
    stockInfo.put("lowPrice", quoteMap.get("l"));
    stockInfo.put("changePercent", quoteMap.get("dp"));
    stockInfo.put("change", quoteMap.get("d"));
    stockInfo.put("currentPrice", quoteMap.get("c"));
    stockInfo.put("volume", altMap.get("regularMarketVolume"));
    stockInfo.put("symbol", symbol);

    return stockInfo;
}





}
