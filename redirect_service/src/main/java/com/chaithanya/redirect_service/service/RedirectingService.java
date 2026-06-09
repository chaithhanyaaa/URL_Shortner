package com.chaithanya.redirect_service.service;

import com.chaithanya.redirect_service.dto.OriginalUrlResponse;
import com.chaithanya.redirect_service.entity.RedirectStats;
import com.chaithanya.redirect_service.exception.ResourceNotFoundException;
import com.chaithanya.redirect_service.repository.RedirectingRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;


import java.util.Optional;


@Service
public class RedirectingService
{

    private final RedirectingRepository repository;
    private final RestTemplate restTemplate;

    public RedirectingService(
            RedirectingRepository repository,
            RestTemplate restTemplate)
    {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }
    @Value("${gateway.url}")
    private String gatewayUrl;

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



    public void updateCount(String shortCode)
    {
        Optional<RedirectStats> stats =
                repository.findByShortCode(shortCode);

        if(stats.isEmpty())
        {
            RedirectStats redirectStats =
                    RedirectStats.builder()
                            .shortCode(shortCode)
                            .clickCount(1L)
                            .build();

            repository.save(redirectStats);
        }
        else
        {
            RedirectStats redirectStats =
                    stats.get();

            redirectStats.setClickCount(
                    redirectStats.getClickCount() + 1
            );

            repository.save(redirectStats);
        }
    }



    public Long getClickCount(String shortCode)
    {
        var val = repository.findByShortCode(shortCode);

        if(val.isPresent())
        {
            return val.get().getClickCount();
        }

        return 0L;
    }
}
