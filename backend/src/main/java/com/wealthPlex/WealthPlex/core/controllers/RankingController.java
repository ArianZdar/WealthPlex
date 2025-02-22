package com.wealthPlex.WealthPlex.core.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.wealthPlex.WealthPlex.ratingSystem.OpenAIStockRating;
import com.wealthPlex.WealthPlex.core.services.StockApiService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
public class RankingController {  

    private final OpenAIStockRating stockRatingService;
    private final StockApiService stockApiService;

    public RankingController(OpenAIStockRating stockRatingService, StockApiService stockApiService) {
        this.stockRatingService = stockRatingService;
        this.stockApiService = stockApiService;
    }

    @GetMapping("/rating/{stockSymbol}")
    public ResponseEntity<String> getStockRating(
        @PathVariable String stockSymbol,
        @RequestParam String username) {
        try { 
            String fullResponse = stockRatingService.getStockExplanation(stockSymbol, username);
            String extractedRating = stockRatingService.extractStockRating(fullResponse);
            return ResponseEntity.ok(extractedRating);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error fetching stock rating: " + e.getMessage());
        }
    }

    @GetMapping("/explanation/{stockSymbol}")
    public ResponseEntity<String> getStockExplanation(
        @PathVariable String stockSymbol,
        @RequestParam String username) {
        try {
            String explanation = stockRatingService.getStockExplanation(stockSymbol, username);
            return ResponseEntity.ok(explanation);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error fetching stock explanation: " + e.getMessage());
        }
    }

    @GetMapping("/info/{stockSymbol}")
    public ResponseEntity<Map<String, Object>> getStockInfo(@PathVariable String stockSymbol) throws FileNotFoundException {
        Map<String, Object> stockData = stockApiService.getStockInfo(stockSymbol);
        
        if (stockData.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(stockData);
        }

        return ResponseEntity.ok(stockData);
    }
}
