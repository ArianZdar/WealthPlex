package com.wealthPlex.WealthPlex.core.controllers;
import com.wealthPlex.WealthPlex.core.DTOs.LoginRequest;
import com.wealthPlex.WealthPlex.core.DTOs.SignupRequest;
import com.wealthPlex.WealthPlex.core.DTOs.StockBuyRequest;
import com.wealthPlex.WealthPlex.core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users/{userId}")
    public Map<String,Object> getUserByUsername(@PathVariable String userId) throws FileNotFoundException {
        return userService.getUserByUsername(userId);
    }
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users/{userId}/stocks")
    public List<Map<String,Object>> getStocksForUser(@PathVariable String userId) throws FileNotFoundException {
        return userService.getUserStocks(userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/users/login")
    public boolean getLoginUser(@RequestBody LoginRequest loginRequest ) {
        userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return true;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/users")
    public Map<String, Object> signUp(@RequestBody SignupRequest signupRequest) {
        return userService.signUp(signupRequest.getUsername(), signupRequest.getPassword());
    }

    @PostMapping("/users/{userId}/investmentType/{longTerm}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> setInvestmentType(@PathVariable String userId, @PathVariable boolean longTerm) {
        return userService.setInvestorType(userId, longTerm);
    }

    @PostMapping("/users/{userId}/stocks/{stockSymbol}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> buyStock(@PathVariable String userId, @PathVariable String stockSymbol,@RequestBody StockBuyRequest buyRequest) throws FileNotFoundException {
        return userService.buyStock(userId,stockSymbol, buyRequest.getPrice(), buyRequest.getAmount());
    }

    @PostMapping("/users/{userId}/stocks/{stockSymbol}/sell/{amount}")
    @ResponseStatus(HttpStatus.OK)
    public double sellStock(@PathVariable String userId, @PathVariable String stockSymbol,@PathVariable int amount) throws FileNotFoundException {
        return userService.sellStock(userId,stockSymbol,amount);
    }

    @PostMapping("/users/{userId}/stocks/{stockSymbol}/amount")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> setStockAmount(@PathVariable String userId, @PathVariable String stockSymbol,@RequestBody int amount) throws FileNotFoundException {
        return userService.setStockAmount(userId,stockSymbol,amount);
    }

    @GetMapping("/users/{userId}/profit")
    @ResponseStatus(HttpStatus.OK)
    public double getProfit(@PathVariable String userId) {
        return userService.getUserProfit(userId);
    }

    @GetMapping("/users/{userId}/stocks/{stock}/profit")
    @ResponseStatus(HttpStatus.OK)
    public double getStockProfit(@PathVariable String userId, @PathVariable String stock) {
        return userService.getProfitOnStock(userId,stock);
    }

    @DeleteMapping("/users/{userId}/watchlist/{symbol}")
    @ResponseStatus(HttpStatus.OK)
    public  List<Map<String,Object>> removeStockFromWatchlist(@PathVariable String userId, @PathVariable String symbol) {
        return userService.removeStockFromWatchlist(userId,symbol);
    }

    @DeleteMapping("/users/{userId}/stocks/{symbol}")
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String,Object>> removeStockFromUser(@PathVariable String userId, @PathVariable String symbol) {
        return userService.removeStockFromUser(userId,symbol);
    }

    @GetMapping("/users/{userId}/watchlist")
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String,Object>> getUserWatchlist(@PathVariable String userId) {
        return userService.getUserWatchlist(userId);
    }

    @GetMapping("/users/{userId}/watchlistR")
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String,Object>> refreshUserWatchlist(@PathVariable String userId) {
        return userService.refreshWatchlistValues(userId);
    }

    @PostMapping("/users/{userId}/watchlist/{symbol}")
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String,Object>> addStockToWatchlist(@PathVariable String userId, @PathVariable String symbol) {
        return userService.addStockToWatchlist(userId,symbol);
    }

    @GetMapping("/users/{userId}/portfolio/price")
    @ResponseStatus(HttpStatus.OK)
    public double getPortfolioPrice(@PathVariable String userId) {
        return userService.getAmountPaid(userId);
    }

    @GetMapping("/users/{userId}/portfolio/value")
    @ResponseStatus(HttpStatus.OK)
    public double getPortfolioValue(@PathVariable String userId) {
        return userService.getPortfolioValue(userId);
    }



}
