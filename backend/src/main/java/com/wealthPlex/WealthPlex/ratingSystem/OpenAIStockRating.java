package com.wealthPlex.WealthPlex.ratingSystem;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
<<<<<<< HEAD
import org.springframework.stereotype.Service;
=======
>>>>>>> 39503fa0fbf0c5d0e69464d040ac3c01ddf13796

import com.wealthPlex.WealthPlex.core.models.User;
import com.wealthPlex.WealthPlex.core.repositories.UserRepository;
import com.wealthPlex.WealthPlex.core.services.UserService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.List;

<<<<<<< HEAD
@Service
=======
>>>>>>> 39503fa0fbf0c5d0e69464d040ac3c01ddf13796
public class OpenAIStockRating {

    private static final String OPENAI_API_KEY = ""; // Replace with actual API key
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    public String getStockRating(String stockSymbol, String username) throws IOException {
        Stock stock = YahooFinance.get(stockSymbol);
        if (stock == null || stock.getQuote() == null) {
            return "Error: Unable to fetch stock data for " + stockSymbol;
        }

        // Extract user details
        User user = (User) userRepository.getFromMap(userService.getUserByUsername(username));
        Boolean isLongTermInvestor = user.isLongTermInvestor();
        String investorType = isLongTermInvestor ? "Long-Term" : "Short-Term";

        // Extract stock details
        BigDecimal price = stock.getQuote().getPrice();
        BigDecimal volatility = getVolatility(stock);

        // Create OpenAI API request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo"); 
        requestBody.put("messages", new Object[]{
            new JSONObject().put("role", "system").put("content", 
                "You are an AI that rates stocks based on the selected investment strategy. "
                + "Short-term ratings prioritize volatility and recent price movements. A short-term investor likes volatile stocks. "
                + "Long-term ratings prioritize stability and historical performance."),
            
            new JSONObject().put("role", "user").put("content",
                "Stock: " + stockSymbol + 
                ", Price: $" + price +
                ", Volatility: " + volatility +  
                ", Investment Type: " + investorType)
        });

        // Send request to OpenAI API
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
            .url(OPENAI_API_URL)
            .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
            .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
            .addHeader("Content-Type", "application/json")
            .build();

        Response response = client.newCall(request).execute();

        // Parse and return OpenAI's response
        if (response.isSuccessful() && response.body() != null) {
            JSONObject responseJson = new JSONObject(response.body().string());
            return responseJson.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        } else {
            return "Error: Failed to get stock rating from AI.";
        }
    }

    public static BigDecimal getVolatility(Stock stock) throws IOException {
        if (stock == null) {
            throw new IOException("Stock data not found for " + stock);
        }

        // Get historical stock prices from the past 30 days
        Calendar from = Calendar.getInstance();
        from.add(Calendar.MONTH, -1); // Get data from one month back
        List<HistoricalQuote> history = stock.getHistory(from, Calendar.getInstance(), Interval.DAILY);

        if (history == null || history.size() < 2) {
            throw new IOException("Not enough historical data for " + stock.getSymbol());
        }

        // Compute Volatility
        return BigDecimal.valueOf(computeStandardDeviation(history));
    }
    
    private static double computeStandardDeviation(List<HistoricalQuote> history) {
        double sumReturns = 0.0;
        double sumSquaredReturns = 0.0;
        int count = 0;

        for (int i = 1; i < history.size(); i++) {
            BigDecimal prevClose = history.get(i - 1).getClose();
            BigDecimal currentClose = history.get(i).getClose();

            if (prevClose == null || currentClose == null) continue;

            // Calculate daily return
            double returnRate = currentClose.subtract(prevClose)
                .divide(prevClose, 10, RoundingMode.HALF_UP)
                .doubleValue();

            sumReturns += returnRate;
            sumSquaredReturns += returnRate * returnRate;
            count++;
        }

        if (count < 1) return 0.0; // Prevent division by zero

        // Compute mean (average return)
        double avgReturn = sumReturns / count;

        // Compute standard deviation (volatility)
        return Math.sqrt((sumSquaredReturns / count) - (avgReturn * avgReturn));
    }
    private double getNetGain(String stockSymbol, String username) throws FileNotFoundException{
        double netGain=0;
        User user = (User) userRepository.getFromMap(userService.getUserByUsername(username));
<<<<<<< HEAD
        
=======
        // then do net percent gain 
>>>>>>> 39503fa0fbf0c5d0e69464d040ac3c01ddf13796

        return netGain;
    }

    public static void main(String[] args) throws IOException {
        
    }
}