package com.chaithanya.shortening_service.controller;


import com.chaithanya.shortening_service.Service.UrlMappingService;
import com.chaithanya.shortening_service.dto.OriginalUrlResponse;
import com.chaithanya.shortening_service.dto.shortUrlResponse;
import com.chaithanya.shortening_service.entity.UrlMapping;
import com.chaithanya.shortening_service.dto.CreateShortUrlRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;


@RestController
public class UrlMappingController
{
    private final UrlMappingService service;
    public UrlMappingController(UrlMappingService service) {
        this.service = service;
    }

    @Value("${gateway.url}")
    private String gatewayUrl;

    @PostMapping("api/shorten")
    public ResponseEntity<shortUrlResponse> createShortUrl(@Valid @RequestBody CreateShortUrlRequest request)
    {
        UrlMapping saved=service.createShortUrl(request.getUrl());
        shortUrlResponse shortUrl=new shortUrlResponse();
        shortUrl.setShortUrl(gatewayUrl+'/'+saved.getShortCode());
        return new ResponseEntity<shortUrlResponse>(shortUrl,HttpStatus.OK);
    }


    //this is used by redirect service
    @GetMapping("/internal/url/{shortCode}")
    public OriginalUrlResponse getOriginalUrl(
            @PathVariable String shortCode)
    {
        String originalUrl =
                service.getOriginalUrl(shortCode);

        return new OriginalUrlResponse(originalUrl);
    }






}
