package com.wealthPlex.WealthPlex;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        try {
            String localIp = InetAddress.getLocalHost().getHostAddress();
            String allowedOrigin = "http://" + localIp + ":3000";
            System.out.println(allowedOrigin);

            registry.addMapping("/**")
                    .allowedOrigins(allowedOrigin, "http://localhost:3000")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowCredentials(true);

            System.out.println("CORS Allowed Origin: " + allowedOrigin);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }}
}