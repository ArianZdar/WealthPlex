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
    HandlerInterceptor loginInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        String localIp = System.getenv("LOCAL_IP");
        if (localIp != null) {
            System.out.println("Running in docker container, local ip is : " + localIp);
        } else {
            System.out.println("Not running in container, may run into issues");
            localIp = "127.0.0.1";
        }


        if (!localIp.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
            throw new IllegalArgumentException("Invalid IP address format");
        }
        String[] parts = localIp.split("\\.");
        String localOrigins = parts[0] + "." + parts[1] + "." + parts[2] + ".*";
        localOrigins = "http://" + localIp + ":3000";
        System.out.println("allowing origin : "+localOrigins);

        System.out.println("---------------------------------Frontend will be deployed on : http://"+localIp+":3000 ---------------------------------");

        registry.addMapping("/**")
                .allowedOrigins(localOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(false)
                .allowedHeaders("*");
        }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor);
    }

    public String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "localhost";
        }
    }


}