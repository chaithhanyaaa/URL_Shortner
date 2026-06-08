package com.chaithanya.urlshortner.Service;


import com.chaithanya.urlshortner.dto.AnalyticsResponse;
import com.chaithanya.urlshortner.entity.UrlMapping;
import com.chaithanya.urlshortner.exception.ResourceNotFoundException;
import com.chaithanya.urlshortner.repository.UrlMappingRepository;
import com.chaithanya.urlshortner.util.Base62Encoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UrlMappingService
{
    private final UrlMappingRepository repository;

    public UrlMappingService(UrlMappingRepository repository) {
        this.repository = repository;
    }


    public UrlMapping createShortUrl(String originalUrl)
    {
        var existing=repository.findByOriginalUrl(originalUrl);
        if(existing.isPresent()){
            return existing.get();
        }

        UrlMapping urlMapping = UrlMapping.builder()
                .originalUrl(originalUrl)
                .createdAt(LocalDateTime.now())
                .clickCount(0L)
                .build();

        UrlMapping saved = repository.save(urlMapping);


        String shortCode = Base62Encoder.encode(saved.getId());

        saved.setShortCode(shortCode);

        return repository.save(saved);
    }


    public String getOriginalUrl(String shortCode) {

        UrlMapping urlMapping = repository
                .findByShortCode(shortCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Short URL not found"));

        return urlMapping.getOriginalUrl();
    }

    public void updateCount(String shortCode)
    {
        UrlMapping urlMapping = repository
                .findByShortCode(shortCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Short URL not found"));
        long currCount=urlMapping.getClickCount();
        urlMapping.setClickCount(currCount+1);
        repository.save(urlMapping);

    }

    public List<UrlMapping> getAllUrls()
    {
        return repository.findAll();
    }

    public AnalyticsResponse getAnalytics(String shortCode)
    {
        UrlMapping urlMapping = repository
                .findByShortCode(shortCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Short URL not found"));

        return new AnalyticsResponse(
                urlMapping.getShortCode(),
                urlMapping.getOriginalUrl(),
                urlMapping.getClickCount()
        );
    }
}
