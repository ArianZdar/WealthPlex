package com.wealthPlex.WealthPlex.core.controllers;
import com.wealthPlex.WealthPlex.core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/users/{userId}")
    public Map<String,Object> getUserByUsername(@PathVariable String userId) throws FileNotFoundException {
        return userService.getUserByUsername(userId);
    }

    @GetMapping("/users/{userId}/stocks")
    public List<Map<String,Object>> getStocksForUser(@PathVariable String userId) throws FileNotFoundException {
        return userService.getUserStocks(userId);
    }

    @GetMapping("/users/login")
    public Map<String,Object> getLoginUser(@RequestBody String username, @RequestBody String password ) throws IllegalAccessException {
        return userService.login(username, password);
    }

    @PostMapping("/users")
    public void signUp(@RequestBody String username, @RequestBody String password) throws IllegalAccessException {
        userService.signUp(username, password);
    }

    @PostMapping("/users/{userId}/investmentType/{longTerm}")
    public Map<String, Object> setInvestmentType(@PathVariable String userId, @PathVariable boolean longTerm) {
        return userService.setInvestorType(userId, longTerm);
    }

}
