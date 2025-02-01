package com.wealthPlex.WealthPlex.core.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {




    @GetMapping("/users/{userId}")
    public String testPath(@PathVariable String userId) {

        return "hiiii";
    }

}
