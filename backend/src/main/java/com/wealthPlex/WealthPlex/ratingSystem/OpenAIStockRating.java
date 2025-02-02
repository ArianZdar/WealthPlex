package com.wealthPlex.WealthPlex.ratingSystem;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import org.json.JSONObject;
import okhttp3.*;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.math.BigDecimal;

@Service
public class OpenAIStockRating {

    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY"); // Use env variable
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public String getStockRating(String stockSymbol, String username) throws IOException {
        Stock stock = YahooFinance.get(stockSymbol);
        if (stock == null || stock.getQuote() == null) {
            return "Error: Unable to fetch stock data for " + stockSymbol;
        }

        BigDecimal price = stock.getQuote().getPrice();
        BigDecimal volatility = getVolatility(stock);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", new Object[]{
            new JSONObject().put("role", "system").put("content", "You are an AI that rates stocks."),
            new JSONObject().put("role", "user").put("content",
                "Stock: " + stockSymbol + ", Price: $" + price +
                ", Volatility: " + volatility + ", User: " + username)
        });

        return sendOpenAIRequest(requestBody, true); // Extract only rating
    }

    public String getStockExplanation(String stockSymbol, String username) throws IOException {
        Stock stock = YahooFinance.get(stockSymbol);
        if (stock == null || stock.getQuote() == null) {
            return "Error: Unable to fetch stock data for " + stockSymbol;
        }

        BigDecimal price = stock.getQuote().getPrice();
        BigDecimal volatility = getVolatility(stock);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", new Object[]{
            new JSONObject().put("role", "system").put("content", 
                "You are an AI stock analyst. You provide two paragraphs explaining why the stock received its rating, "
                + "mentioning key factors such as volatility, fundamentals, market sentiment, "
                + "and upcoming earnings events. Also, discuss analyst expectations and recent news-driven targets."),
            new JSONObject().put("role", "user").put("content",
                "Stock: " + stockSymbol + ", Price: $" + price +
                ", Volatility: " + volatility + ", User: " + username)
        });

        return sendOpenAIRequest(requestBody, false); // Return full AI response
    }

    
    //Sends a request to OpenAI and returns either the rating or full explanation.
    private String sendOpenAIRequest(JSONObject requestBody, boolean extractRatingOnly) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
            .url(OPENAI_API_URL)
            .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
            .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
            .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful() && response.body() != null) {
            JSONObject responseJson = new JSONObject(response.body().string());
            String content = responseJson.getJSONArray("choices")
                                         .getJSONObject(0)
                                         .getJSONObject("message")
                                         .getString("content");

            return extractRatingOnly ? extractStockRating(content) : content;
        } else {
            return "Error: Failed to get response from AI.";
        }
    }

    private String extractStockRating(String aiResponse) {
        return aiResponse.replaceAll(".*?(\\b\\d+/\\d+\\b).*", "$1");
    }

    private BigDecimal getVolatility(Stock stock) {
        return BigDecimal.valueOf(Math.random() * 10); // Replace with real calculation
    }
}
