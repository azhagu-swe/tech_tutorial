package com.azhag_swe.tech_tutorial.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final int authRequestsPerMinute;
    private final int apiRequestsPerMinute;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RateLimitingFilter(
            @Value("${app.rate-limit.auth:10}") int authRequestsPerMinute,
            @Value("${app.rate-limit.api:100}") int apiRequestsPerMinute) {
        this.authRequestsPerMinute = authRequestsPerMinute;
        this.apiRequestsPerMinute = apiRequestsPerMinute;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String endpointKey = getEndpointKey(request);
        Bucket bucket = buckets.computeIfAbsent(endpointKey, this::createBucket);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            response.setHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            // Calculate the wait time in seconds
            long retryAfterSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000;
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));

            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("timestamp", System.currentTimeMillis());
            errorBody.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
            errorBody.put("error", "Too Many Requests");
            errorBody.put("message", "Rate limit exceeded. Try again in " + retryAfterSeconds + " seconds");
            errorBody.put("path", request.getRequestURI());

            objectMapper.writeValue(response.getOutputStream(), errorBody);
        }
    }

    private String getEndpointKey(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth")) {
            return "auth_" + getClientIP(request);
        }
        return "api_" + getClientIP(request);
    }

    private Bucket createBucket(String key) {
        int capacity = key.startsWith("auth_") ? authRequestsPerMinute : apiRequestsPerMinute;
        return Bucket.builder()
                .addLimit(Bandwidth.classic(capacity, Refill.intervally(capacity, Duration.ofMinutes(1))))
                .build();
    }

    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        return ip != null ? ip.split(",")[0] : request.getRemoteAddr();
    }
}
