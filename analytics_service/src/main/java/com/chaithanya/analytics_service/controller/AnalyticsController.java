package com.chaithanya.analytics_service.controller;

import com.chaithanya.analytics_service.entity.UrlMapping;
import com.chaithanya.analytics_service.service.AnalyticsService;
import com.chaithanya.analytics_service.dto.AnalyticsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AnalyticsController {

    AnalyticsService service;
    AnalyticsController(AnalyticsService service)
    {
        this.service=service;
    }

    @GetMapping("/api/analytics/{shortCode}")
    public AnalyticsResponse getAnalytics(
            @PathVariable String shortCode)
    {
        return service.getAnalytics(shortCode);
    }


}
