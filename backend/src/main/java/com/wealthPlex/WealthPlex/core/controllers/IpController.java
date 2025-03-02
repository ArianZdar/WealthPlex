package com.wealthPlex.WealthPlex.core.controllers;

import com.wealthPlex.WealthPlex.WebConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IpController {

    @Autowired
    private WebConfig webConfig;

    @GetMapping("/api/ip")
    public String getLocalIp() {
        return webConfig.getLocalIp();
    }
}