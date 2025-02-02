package com.wealthPlex.WealthPlex.core.controllers;

import org.springframework.web.bind.annotation.*;

import com.wealthPlex.WealthPlex.ratingSystem.OpenAIStockRating;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.io.IOException;

@RestController
@RequestMapping("/api/stocks")
public class rankingController {

    private final OpenAIStockRating stockRatingService;

    public rankingController(OpenAIStockRating stockRatingService) {
        this.stockRatingService = stockRatingService;
    }

    @GetMapping("/rating/{stockSymbol}")
    public ResponseEntity<String> getStockRating(
        @PathVariable String stockSymbol,
        @RequestParam String username) {
            try { 
                String rating = stockRatingService.getStockRating(stockSymbol, username);
                return ResponseEntity.ok(rating);
            }
            catch (IOException e){
                return ResponseEntity.status(500).body("Error fetching stock rating: " + e.getMessage());
            }
        }

    @GetMapping("/explanation/{stockSymbol}")
    public ResponseEntity<String> getStockExplanation(
        @PathVariable String stockSymbol,
        @RequestParam String username) throws IOException {

            try {
                String explanation = stockRatingService.getStockExplanation(stockSymbol, username);
                return ResponseEntity.ok(explanation);
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Error fetching stock explanation: " + e.getMessage());
            }
        }
    }