package com.wealthPlex.WealthPlex.core.controllers;

import org.springframework.web.bind.annotation.*;
import com.wealthPlex.WealthPlex.ratingSystem.OpenAIStockRating;
import org.springframework.http.ResponseEntity;
import java.io.IOException;

@RestController
@RequestMapping("/api/stocks")
public class RankingController {  

    private final OpenAIStockRating stockRatingService;

    public RankingController(OpenAIStockRating stockRatingService) {
        this.stockRatingService = stockRatingService;
    }

    @GetMapping("/rating/{stockSymbol}")
    public ResponseEntity<String> getStockRating(
        @PathVariable String stockSymbol,
        @RequestParam String username) {
        try { 
           
            String fullResponse = stockRatingService.getStockExplanation(stockSymbol, username);
            String extractedRating = stockRatingService.extractStockRating(fullResponse);

            return ResponseEntity.ok("Stock Rating: " + extractedRating);
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

    @GetMapping("/recommendation/{stockSymbol}")
    public ResponseEntity<String> getStockRecommendation(
        @PathVariable String stockSymbol, @RequestParam String username) {
        try {
            String recommendation = stockRatingService.getStockRecommendation(stockSymbol, username);
            return ResponseEntity.ok(recommendation);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error fetching recommendation: " + e.getMessage());
        }
    }
}
