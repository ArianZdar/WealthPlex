package com.wealthPlex.WealthPlex.core.controllers;
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
    @GetMapping("/users/login")
    public Map<String,Object> getLoginUser(@RequestBody String username, @RequestBody String password ) throws IllegalAccessException {
        return userService.login(username, password);
    }
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/users")
    public void signUp(@RequestBody String username, @RequestBody String password) throws IllegalAccessException {
        userService.signUp(username, password);
    }

    @PostMapping("/users/{userId}/investmentType/{longTerm}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> setInvestmentType(@PathVariable String userId, @PathVariable boolean longTerm) {
        return userService.setInvestorType(userId, longTerm);
    }

    @PostMapping("/users/{userId}/stocks/{stockSymbol}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> buyStock(@PathVariable String userId, @PathVariable String stockSymbol,@RequestBody double price,@RequestBody int amount) throws FileNotFoundException {
        return userService.buyStock(userId,stockSymbol,price,amount);
    }

    @PostMapping("/users/{userId}/stocks/{stockSymbol}/sell")
    @ResponseStatus(HttpStatus.OK)
    public double sellStock(@PathVariable String userId, @PathVariable String stockSymbol,@RequestBody double price,@RequestBody int amount) throws FileNotFoundException {
        return userService.sellStock(userId,stockSymbol,price,amount);
    }

    @PostMapping("/users/{userId}/stocks/{stockSymbol}/amount")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> setStockAmount(@PathVariable String userId, @PathVariable String stockSymbol,@RequestBody int amount) throws FileNotFoundException {
        return userService.setStockAmount(userId,stockSymbol,amount);
    }

    @GetMapping("users/{userId}/profit")
    @ResponseStatus(HttpStatus.OK)
    public double getProfit(@PathVariable String userId) {
        return userService.getUserProfit(userId);
    }

}
