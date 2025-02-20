package com.wealthPlex.WealthPlex;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger LOGGING_INTERCEPTOR = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    // Logs HTTP method and URI before the request is handled
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        LOGGING_INTERCEPTOR.info("Request: {} {}", request.getMethod(), request.getRequestURI());
        return true;  // Allows the request to proceed
    }

    @Override
    // Logs response status and URI after request completion. Logs exceptions if any
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        LOGGING_INTERCEPTOR.info("Response: {} {}", response.getStatus(), request.getRequestURI());
        if (ex != null) {
            // Logs any exception
            LOGGING_INTERCEPTOR.error("Exception: ", ex);
        }
    }
}

