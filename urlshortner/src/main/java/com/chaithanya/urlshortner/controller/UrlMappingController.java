package com.chaithanya.urlshortner.controller;


import com.chaithanya.urlshortner.Service.UrlMappingService;
import com.chaithanya.urlshortner.dto.AnalyticsResponse;
import com.chaithanya.urlshortner.dto.shortUrlResponse;
import com.chaithanya.urlshortner.entity.UrlMapping;
import com.chaithanya.urlshortner.dto.CreateShortUrlRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@RestController
public class UrlMappingController
{
    private final UrlMappingService service;
    public UrlMappingController(UrlMappingService service) {
        this.service = service;
    }

    @PostMapping("api/shorten")
    public ResponseEntity<shortUrlResponse> createShortUrl(@Valid @RequestBody CreateShortUrlRequest request)
    {
        System.out.println(request.getUrl()+" reached controller");
        UrlMapping saved=service.createShortUrl(request.getUrl());
        shortUrlResponse shortUrl=new shortUrlResponse();
        shortUrl.setShortUrl("http://localhost:8080/"+saved.getShortCode());
        return new ResponseEntity<shortUrlResponse>(shortUrl,HttpStatus.OK);
    }

    @GetMapping("api/urls")
    public List<UrlMapping> getAllUrls() {
        return service.getAllUrls();
    }


    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode)
    {

        String originalUrl = service.getOriginalUrl(shortCode);
        service.updateCount(shortCode);


        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(originalUrl));

        return new ResponseEntity<>(
                headers,
                HttpStatus.FOUND
        );
    }

    @GetMapping("/api/analytics/{shortCode}")
    public ResponseEntity<AnalyticsResponse> getAnalytics(
            @PathVariable String shortCode
    ) {
        return ResponseEntity.ok(
                service.getAnalytics(shortCode)
        );
    }


}
