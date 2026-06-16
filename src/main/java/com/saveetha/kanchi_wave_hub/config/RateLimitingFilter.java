package com.saveetha.kanchi_wave_hub.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter implements Filter {

    private final Map<String, RequestCounter> limitMap = new ConcurrentHashMap<>();
    
    @org.springframework.beans.factory.annotation.Value("${app.rate-limit:50}")
    private int limit;

    private static final long TIME_WINDOW_MS = 1000; // 1 second window

    private static class RequestCounter {
        final long windowStart;
        final AtomicInteger count;

        RequestCounter(long windowStart) {
            this.windowStart = windowStart;
            this.count = new AtomicInteger(1);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        // Skip rate limiting for static image endpoints and actuator metrics to avoid performance testing issues
        if (path.contains("/images/") || path.contains("/actuator")) {
            chain.doFilter(request, response);
            return;
        }

        String ip = httpRequest.getRemoteAddr();
        // If a JWT token is present in header, we rate limit based on token to be more granular
        String authHeader = httpRequest.getHeader("Authorization");
        String limitKey = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader : ip;

        long now = System.currentTimeMillis();

        RequestCounter counter = limitMap.compute(limitKey, (key, current) -> {
            if (current == null || (now - current.windowStart) > TIME_WINDOW_MS) {
                return new RequestCounter(now);
            } else {
                current.count.incrementAndGet();
                return current;
            }
        });

        if (counter.count.get() > limit) {
            httpResponse.setStatus(429); // Too Many Requests
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"status\":429,\"message\":\"Too Many Requests. Rate limit exceeded.\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    public void reset() {
        limitMap.clear();
    }

    @Override
    public void destroy() {
        reset();
    }
}
