package com.wealthPlex.WealthPlex.ratingSystem;

import org.json.JSONObject;
import okhttp3.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.wealthPlex.WealthPlex.core.models.User;
import com.wealthPlex.WealthPlex.core.services.StockApiService;
import com.wealthPlex.WealthPlex.core.services.UserService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.regex.Pattern;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class OpenAIStockRating {
    @Autowired
    private StockApiService stockApiService;

    @Autowired
    private UserService userService;

    private static final String ENV_PATH = "/Users/hamzadaqa/Desktop/WealthPlex/WealthPlex/backend";

    User user = new User();

    private static final Dotenv dotenv = Dotenv.configure()
        .directory(ENV_PATH)
        .ignoreIfMalformed()
        .ignoreIfMissing()
        .load();

    private static final String OPENAI_API_KEY = dotenv.get("OPENAI_API_KEY");
    private static final String FINNHUB_API_KEY = "cuv94chr01qpi6ru2aggcuv94chr01qpi6ru2ah0";
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    static {
        System.out.println("OpenAI API Key: " + OPENAI_API_KEY);
    }

    
    public String getStockRating(String stockSymbol, String username) throws IOException {
        String fullResponse = getStockExplanation(stockSymbol, username);
        return extractStockRating(fullResponse);  
    }

    public String getStockExplanation(String stockSymbol, String investmentType) throws IOException {
        try {
            String earningsDate;
            try {
                earningsDate = stockApiService.getNextEarningsDate(stockSymbol);
                if (earningsDate == null) {
                    earningsDate = "Expected in the next quarter";
                }
            } catch (Exception e) {
                System.out.println("Error getting earnings date: " + e.getMessage());
                earningsDate = "Expected in the next quarter";
            }

            // Create investment-type specific guidance
            String investmentGuidance = investmentType.equalsIgnoreCase("long-term") ?
                "Focus on fundamental strength, long-term growth potential, competitive advantages, and sustainable business models. "
                + "Consider factors like market leadership, R&D investments, industry trends, and long-term market opportunities. "
                + "Pay special attention to the company's financial health, debt levels, and cash flow sustainability."
                :
                "Focus on technical indicators, momentum signals, short-term catalysts, and market sentiment. "
                + "Consider factors like trading volume, price action, upcoming events, and short-term market trends. "
                + "Pay special attention to volatility patterns and near-term price movements.";

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("messages", new Object[]{
                new JSONObject().put("role", "system").put("content",
                    "You are a financial analyst providing personalized stock analysis for a " + investmentType + " investor. "
                    + "Your response must start with a numerical rating in the exact format:\n"
                    + "RATING: X/10\n\n"
                    + "where X is a number between 0 and 10 based on the stock's potential for " + investmentType + " investment.\n\n"
                    + "After the rating, provide a well-organized analysis with these sections:\n\n"
                    + "Market Sentiment:\n"
                    + "• Current market position\n"
                    + "• Recent price trends\n"
                    + "• Market sentiment indicators\n\n"
                    + "Technical Analysis:\n"
                    + "• Key price levels\n"
                    + "• Trading patterns\n"
                    + "• Volume analysis\n\n"
                    + "Fundamental Factors:\n"
                    + "• Financial metrics\n"
                    + "• Company developments\n"
                    + "• Industry position\n\n"
                    + "Earnings Outlook:\n"
                    + "• Next earnings date\n"
                    + "• Recent performance\n"
                    + "• Analyst expectations\n\n"
                    + "Risk Assessment:\n"
                    + "• Key risks\n"
                    + "• Market challenges\n"
                    + "• Potential headwinds\n\n"
                    + investmentGuidance),

                new JSONObject().put("role", "user").put("content",
                    "Analyze " + stockSymbol + " stock specifically for a " + investmentType + " investor and provide a rating out of 10. "
                    + "The next earnings report is on " + earningsDate + ". "
                    + (investmentType.equalsIgnoreCase("long-term") ?
                        "Consider long-term growth potential, competitive advantages, and fundamental strength in your analysis."
                        :
                        "Consider short-term catalysts, technical indicators, and immediate market opportunities in your analysis.")
                )
            });

            String response = sendOpenAIRequest(requestBody);
            System.out.println("OpenAI Response: " + response);
            return response;
        } catch (Exception e) {
            System.out.println("Error in getStockExplanation: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    private BigDecimal getStockPrice(String stockSymbol) throws IOException {
        String url = "https://finnhub.io/api/v1/quote?symbol=" + stockSymbol + "&token=" + FINNHUB_API_KEY;

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
        if (!json.has("c")) {
            return null;
        }

        return new BigDecimal(json.optString("c", "0"));
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
            System.out.println(" OpenAI API Error: " + response.code() + " - " + response.message());
            System.out.println(" Response Body: " + (response.body() != null ? response.body().string() : "null"));
            return "Error: Failed to get response from AI.";
        }

        JSONObject responseJson = new JSONObject(response.body().string());
        return responseJson.getJSONArray("choices")
                           .getJSONObject(0)
                           .getJSONObject("message")
                           .getString("content");
    }

    public String extractStockRating(String aiResponse) {
        //REGEX
        Pattern pattern = Pattern.compile("(\\d+(\\.\\d+)?)/10");
        java.util.regex.Matcher matcher = pattern.matcher(aiResponse);
    
        if (matcher.find()) {
            return matcher.group(1) + "/10";  // extract rating
        }
    
        // If no rating found, return a default message
        return "Error: No valid rating found in AI response.";
    }

    private BigDecimal getVolatility() {
        return BigDecimal.valueOf(Math.random() * 10);
    }
}
