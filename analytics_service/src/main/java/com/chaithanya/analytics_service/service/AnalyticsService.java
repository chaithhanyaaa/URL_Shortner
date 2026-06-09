package com.chaithanya.analytics_service.service;

import com.chaithanya.analytics_service.dto.ClickCountResponse;
import com.chaithanya.analytics_service.dto.OriginalUrlResponse;
import com.chaithanya.analytics_service.exception.ResourceNotFoundException;
import com.chaithanya.analytics_service.repository.AnalyticsRepository;
import com.chaithanya.analytics_service.dto.AnalyticsResponse;
import com.chaithanya.analytics_service.entity.UrlMapping;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Service
public class AnalyticsService
{

    AnalyticsRepository repository;
    private final RestTemplate restTemplate;
    AnalyticsService(AnalyticsRepository repository,RestTemplate restTemplate)
    {
        this.restTemplate=restTemplate;
        this.repository=repository;
    }
    @Value("${gateway.url}")
    private String gatewayUrl;

    public AnalyticsResponse getAnalytics(
            String shortCode)
    {
        String originalUrl =
                getOriginalUrl(shortCode);

        Long clickCount =
                getClickCount(shortCode);

        return AnalyticsResponse.builder()
                .shortCode(gatewayUrl+'/'+shortCode)
                .originalUrl(originalUrl)
                .clickCount(clickCount)
                .build();
    }


    // this calls shortening service api
    public String getOriginalUrl(String shortCode)
    {
        try
        {
            String url =
                    "http://localhost:8080/internal/url/" + shortCode;

            OriginalUrlResponse response =
                    restTemplate.getForObject(
                            url,
                            OriginalUrlResponse.class
                    );

            return response.getOriginalUrl();
        }
        catch(HttpClientErrorException.NotFound ex)
        {
            throw new ResourceNotFoundException(
                    "Short URL not found"
            );
        }
    }


    // this calls redirect service api
    public Long getClickCount(String shortCode)
    {
        try
        {
            String url =
                    "http://localhost:8081/internal/clicks/" + shortCode;

            ClickCountResponse response =
                    restTemplate.getForObject(
                            url,
                            ClickCountResponse.class
                    );

            return response.getClickCount();
        }
        catch(HttpClientErrorException.NotFound ex)
        {
            throw new ResourceNotFoundException(
                    "Short URL not found"
            );
        }
    }


}
