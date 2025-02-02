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

    // ✅ Static block to print API keys during class loading
    static {
        System.out.println("✅ OpenAI API Key: " + OPENAI_API_KEY);
        System.out.println("✅ Alpha Vantage API Key: " + ALPHA_VANTAGE_API_KEY);
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
                "You are a financial analyst. Given stock data, provide a **detailed** analysis in 1-2 paragraphs. " +
                "Discuss the stock's volatility, upcoming earnings reports, and market sentiment. " +
                "Mention recent news that may impact the stock price, whether positively or negatively. " +
                "Do not give a simple rating, but a **full analysis**."),
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
        return new BigDecimal(quote.optString("05. price", "0"));
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
    
        if (!response.isSuccessful()) {
            System.out.println("❌ OpenAI API Error: " + response.code() + " - " + response.message());
            System.out.println("🔍 Response Body: " + (response.body() != null ? response.body().string() : "null"));
            return "Error: Failed to get response from AI.";
        }
    
        JSONObject responseJson = new JSONObject(response.body().string());
        String content = responseJson.getJSONArray("choices")
                                     .getJSONObject(0)
                                     .getJSONObject("message")
                                     .getString("content");
    
        return extractRatingOnly ? extractStockRating(content) : content;
    }
    
    private String extractStockRating(String aiResponse) {
        return aiResponse.replaceAll(".*?(\\b\\d+/\\d+\\b).*", "$1");
    }

    private BigDecimal getVolatility() {
        return BigDecimal.valueOf(Math.random() * 10);
    }
}
