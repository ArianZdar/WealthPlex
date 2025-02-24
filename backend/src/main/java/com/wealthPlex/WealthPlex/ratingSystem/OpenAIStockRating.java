package com.wealthPlex.WealthPlex.ratingSystem;

import org.json.JSONObject;
import okhttp3.*;
import org.springframework.stereotype.Service;

import com.wealthPlex.WealthPlex.core.models.User;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.regex.Pattern;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class OpenAIStockRating {
    private static final String ENV_PATH = "/Users/hamzadaqa/Desktop/WealthPlex/WealthPlex/backend";

    User user = new User();

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

    
    public String getStockRating(String stockSymbol, String username) throws IOException {
        String fullResponse = getStockExplanation(stockSymbol, username);
        return extractStockRating(fullResponse);  
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
                "You are a financial analyst. Given the stock name and the type of investor you are advising for **in this case they are a"+
                user.getInvestmentType()+
                "***provide a **detailed** analysis in 2 paragraphs. You must also look on the internet for the company's financial"+
                "performance, market trends, upcoming earning dates with the market sentiment for these upcoming earnings and growth "+
                "prospects and conclude with a statement on if this is a good investment for a "+
                user.getInvestmentType()+
                "Discuss the stock's volatility, upcoming earnings reports, and market sentiment. " +
                "Mention recent news that may impact the stock price, whether positively or negatively. " +
                "You Must give a rating out of 10 **in the format of X/10, and you must provide an in depth analysis"),
                new JSONObject().put("role", "user").put("content",
                "Stock: " + stockSymbol + ", Price: $" + price + ", User: " + username +
                ". Should a "+
                user.getInvestmentType()+
                "consider this stock? Why?")
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

    public String extractStockRating(String aiResponse) {
        //REGEX to extract numbers followed by "/10"
        Pattern pattern = Pattern.compile("(\\d+(\\.\\d+)?)/10");
        java.util.regex.Matcher matcher = pattern.matcher(aiResponse);
    
        if (matcher.find()) {
            return matcher.group(1) + "/10";  // Extracted rating
        }
    
        // If no rating found, return a default message
        return "Error: No valid rating found in AI response.";
    }

    private BigDecimal getVolatility() {
        return BigDecimal.valueOf(Math.random() * 10);
    }
}
