package com.wealthPlex.WealthPlex.ratingSystem;

import org.json.JSONObject;
import okhttp3.*;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.math.BigDecimal;
import io.github.cdimascio.dotenv.Dotenv;


@Service
public class OpenAIStockRating {
    private static final String ENV_PATH = "/Users/hamzadaqa/Desktop/WealthPlex/WealthPlex/backend";

    private static final Dotenv dotenv = Dotenv.configure()
        .directory(ENV_PATH)  // Set directory explicitly
        .ignoreIfMalformed()  
        .ignoreIfMissing()    
        .load();

    private static final String OPENAI_API_KEY = dotenv.get("OPENAI_API_KEY");
    private static final String ALPHA_VANTAGE_API_KEY = dotenv.get("ALPHA_VANTAGE_API_KEY"); 

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String ALPHA_VANTAGE_URL = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=";

    static {
        if (OPENAI_API_KEY == null || OPENAI_API_KEY.isEmpty()) {
            throw new RuntimeException("❌ Missing OpenAI API Key. Check your .env file.");
        }
        if (ALPHA_VANTAGE_API_KEY == null || ALPHA_VANTAGE_API_KEY.isEmpty()) {
            throw new RuntimeException("❌ Missing Alpha Vantage API Key. Check your .env file.");
        }

        System.out.println("✅ OpenAI API Key Loaded");
        System.out.println("✅ Alpha Vantage API Key Loaded");
    }

    public String getStockRating(String stockSymbol, String username) throws IOException {
        BigDecimal price = getStockPrice(stockSymbol);
        if (price == null) {
            return "Error: Unable to fetch stock data for " + stockSymbol;
        }

        BigDecimal volatility = getVolatility();

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", new Object[]{
            new JSONObject().put("role", "system").put("content", "You are an AI that rates stocks."),
            new JSONObject().put("role", "user").put("content",
                "Stock: " + stockSymbol + ", Price: $" + price +
                ", Volatility: " + volatility + ", User: " + username)
        });

        return sendOpenAIRequest(requestBody, true);
    }

    public String getStockExplanation(String stockSymbol, String username) throws IOException {
        BigDecimal price = getStockPrice(stockSymbol);
        if (price == null) {
            return "Error: Unable to fetch stock data for " + stockSymbol;
        }

        BigDecimal volatility = getVolatility();

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

        return sendOpenAIRequest(requestBody, false);
    }

    private BigDecimal getStockPrice(String stockSymbol) throws IOException {
        String url = ALPHA_VANTAGE_URL + stockSymbol + "&apikey=" + ALPHA_VANTAGE_API_KEY;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful() || response.body() == null) {
            return null;
        }

        JSONObject json = new JSONObject(response.body().string());
        if (!json.has("Global Quote")) {
            return null;
        }

        JSONObject quote = json.getJSONObject("Global Quote");
        String priceStr = quote.optString("05. price", "");
        return priceStr.isEmpty() ? null : new BigDecimal(priceStr);
    }

    private String sendOpenAIRequest(JSONObject requestBody, boolean extractRatingOnly) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();
    
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful() || response.body() == null) {
            return "Error: Failed to get response from AI.";
        }

        String responseBody = response.body().string();
        JSONObject responseJson = new JSONObject(responseBody);
        String content = responseJson.getJSONArray("choices")
                                     .getJSONObject(0)
                                     .getJSONObject("message")
                                     .optString("content", "Error: No content received.");
    
        return extractRatingOnly ? extractStockRating(content) : content;
    }

    private String extractStockRating(String aiResponse) {
        String rating = aiResponse.replaceAll(".*?(\\b\\d+/\\d+\\b).*", "$1");
        return rating.isEmpty() ? "N/A" : rating;
    }

    private BigDecimal getVolatility() {
        return BigDecimal.valueOf(Math.random() * 10);
    }
}
