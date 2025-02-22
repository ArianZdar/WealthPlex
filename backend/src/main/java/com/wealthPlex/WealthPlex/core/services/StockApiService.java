package com.wealthPlex.WealthPlex.core.services;

import com.google.api.gax.rpc.NotFoundException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
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


    public Map<String,Object> getBestMatchSearch(String keyword) {
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
        Map<String, Object> resultMap = data.toMap();
        return resultMap;
    }

    public List<String> getMatches(String symbol) {
        Map<String, Object> data = getBestMatchSearch(symbol);
    
        System.out.println("API Response: " + data.toString());
        if (data.get("count").toString().equals("0")) return List.of();

        List<Map<String, Object>> results= (List<Map<String, Object>>) data.get("result");
        return results.stream().map(item -> item.get("symbol").toString()).toList();
    }
    public Map<String, Object> queryStockHistory(String symbol) throws FileNotFoundException {
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
        if (results == null) {
            throw new FileNotFoundException("No results found for symbol: " + symbol);
        }
        Map<String,Object> info = (Map<String, Object>) results.get(0);
        return info;
    }

    public Map<String, Object> getStockHistory(String symbol) throws FileNotFoundException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Map<String, Object> query = queryStockHistory(symbol);
        List<Integer> timeStamps = (List<Integer>) query.get("timestamp");
        HashMap<String, Object> indicators = (HashMap<String, Object>) query.get("indicators");
        List<HashMap<String, List<Object>>> quotes = (List<HashMap<String, List<Object>>>) indicators.get("quote");

        List<Map<String, Object>> historyList = IntStream.range(0,timeStamps.size()).mapToObj((num -> {
            Map<String, Object> stockHistory = new HashMap<>();
            stockHistory.put("price", Float.parseFloat(quotes.get(0).get("close").get(num).toString()));
            long timeStamp = Long.parseLong(timeStamps.get(num).toString());
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timeStamp), ZoneId.of("UTC"));
            String formattedDate = dateTime.format(formatter);
            stockHistory.put("date", formattedDate);
            return stockHistory;
        })).toList();

        List<Float> priceList = historyList.stream().map(map -> Float.parseFloat(map.get("price").toString())).toList();
        List<String> dateList = historyList.stream().map(map -> (map.get("date").toString())).toList();

        Map<String, Object> stockHistory = new HashMap<>();
        stockHistory.put("price", priceList);
        stockHistory.put("date", dateList);

        return stockHistory;

    }

    
    


    public Map<String, Object> getStockInfo(String symbol) throws FileNotFoundException {

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
