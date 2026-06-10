package com.chaithanya.redirect_service.service;

import com.chaithanya.redirect_service.dto.OriginalUrlResponse;
import com.chaithanya.redirect_service.entity.RedirectStats;
import com.chaithanya.redirect_service.exception.ResourceNotFoundException;
import com.chaithanya.redirect_service.repository.RedirectingRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
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
    private final RedisTemplate<String,String> redisTemplate;

    public RedirectingService(
            RedirectingRepository repository,
            RestTemplate restTemplate,
            RedisTemplate<String,String> redisTemplate
    )
    {
        this.repository = repository;
        this.restTemplate = restTemplate;
        this.redisTemplate=redisTemplate;
    }

    @Value("${gateway.url}")
    private String gatewayUrl;

    // this calls shortening service api
    public String getOriginalUrl(String shortCode)
    {
        long start = System.currentTimeMillis();

        try
        {
            String cachedUrl =
                    redisTemplate.opsForValue()
                            .get(shortCode);

            if(cachedUrl != null)
            {
                System.out.println("CACHE HIT");

                System.out.println(
                        "getOriginalUrl took = "
                                + (System.currentTimeMillis() - start)
                                + " ms"
                );

                return cachedUrl;
            }

            System.out.println("CACHE MISS");
        }
        catch(Exception ex)
        {
            System.out.println(
                    "Redis unavailable. Falling back to Shortening Service"
            );
        }

        try
        {
            String url =
                    "http://localhost:8080/internal/url/" + shortCode;

            OriginalUrlResponse response =
                    restTemplate.getForObject(
                            url,
                            OriginalUrlResponse.class
                    );

            try
            {
                redisTemplate.opsForValue().set(
                        shortCode,
                        response.getOriginalUrl()
                );

                System.out.println("stored in cache");
            }
            catch(Exception ex)
            {
                System.out.println(
                        "Redis unavailable. Skipping cache update"
                );
            }

            System.out.println(
                    "getOriginalUrl took = "
                            + (System.currentTimeMillis() - start)
                            + " ms"
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

    @Async
    public void updateCount(String shortCode)
    {
        System.out.println(
                "updateCount thread = "
                        + Thread.currentThread().getName()
        );

        var stats = repository.findByShortCode(shortCode);

        if(stats.isPresent())
        {
            RedirectStats redirectStats = stats.get();

            redirectStats.setClickCount(
                    redirectStats.getClickCount() + 1
            );

            repository.save(redirectStats);
        }
        else
        {
            RedirectStats redirectStats =
                    RedirectStats.builder()
                            .shortCode(shortCode)
                            .clickCount(1L)
                            .build();

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
