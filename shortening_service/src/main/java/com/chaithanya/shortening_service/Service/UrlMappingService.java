package com.chaithanya.shortening_service.Service;


import com.chaithanya.shortening_service.entity.UrlMapping;
import com.chaithanya.shortening_service.exception.UrlNotFoundException;
import com.chaithanya.shortening_service.repository.UrlMappingRepository;
import com.chaithanya.shortening_service.util.Base62Encoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
                .build();

        UrlMapping saved = repository.save(urlMapping);
        String shortCode = Base62Encoder.encode(saved.getId());
        saved.setShortCode(shortCode);
        return repository.save(saved);
    }




    public String getOriginalUrl(String shortCode)
    {
        UrlMapping urlMapping = repository
                .findByShortCode(shortCode)
                .orElseThrow(() ->
                        new UrlNotFoundException("Short URL not found"));

        return urlMapping.getOriginalUrl();
    }









}
