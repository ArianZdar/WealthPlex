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
        .directory(ENV_PATH)
        .ignoreIfMalformed()
        .ignoreIfMissing()
        .load();

    private static final String OPENAI_API_KEY = dotenv.get("OPENAI_API_KEY");
    private static final String ALPHA_VANTAGE_API_KEY = dotenv.get("ALPHA_VANTAGE_API_KEY");

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String ALPHA_VANTAGE_URL = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=";

    static {
        System.out.println("✅ OpenAI API Key: " + OPENAI_API_KEY);
        System.out.println("✅ Alpha Vantage API Key: " + ALPHA_VANTAGE_API_KEY);
    }

    // ✅ Now `getStockRating()` extracts rating from AI response
    public String getStockRating(String stockSymbol, String username) throws IOException {
        String fullResponse = getStockExplanation(stockSymbol, username);
        return extractStockRating(fullResponse);  // Extracts only the rating from AI response
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
                "You are a financial analyst. Given the stock name and the type of investor you are advising for (short term or long term), provide a **detailed** analysis in 1-2 paragraphs. You must also look on the internet for the company's financial performance, market trends, and growth prospects and conclude with a rating out of 10 on if this is good for the selected type of investor." +
                "Discuss the stock's volatility, upcoming earnings reports, and market sentiment. " +
                "Mention recent news that may impact the stock price, whether positively or negatively. " +
                "Do not give a simple rating, but a **full analysis**."),
            new JSONObject().put("role", "user").put("content",
                "Stock: " + stockSymbol + ", Price: $" + price +
                ", Volatility: " + volatility + ", User: " + username)
        });

        return sendOpenAIRequest(requestBody);
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

    private String sendOpenAIRequest(JSONObject requestBody) throws IOException {
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
        return responseJson.getJSONArray("choices")
                           .getJSONObject(0)
                           .getJSONObject("message")
                           .getString("content");
    }

    // ✅ Make extractStockRating PUBLIC so the controller can call it
    public String extractStockRating(String aiResponse) {
        return aiResponse.replaceAll(".*?(\\b\\d+/10\\b).*", "$1");  // Extracts rating (e.g., 8/10)
    }

    private BigDecimal getVolatility() {
        return BigDecimal.valueOf(Math.random() * 10);
    }
}
