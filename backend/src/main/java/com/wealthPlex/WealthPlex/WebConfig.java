package com.wealthPlex.WealthPlex;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    HandlerInterceptor loggingInterceptor;

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

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }}

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor);
    }
}