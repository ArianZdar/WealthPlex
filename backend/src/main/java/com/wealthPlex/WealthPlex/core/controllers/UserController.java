package com.wealthPlex.WealthPlex.core.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {




    @GetMapping("/users/test")
    public String testPath() {
        return "hiiii";
    }

}
