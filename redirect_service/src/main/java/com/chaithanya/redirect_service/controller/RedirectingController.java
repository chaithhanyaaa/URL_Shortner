




package com.chaithanya.redirect_service.controller;

import com.chaithanya.redirect_service.dto.ClickCountResponse;
import com.chaithanya.redirect_service.service.RedirectingService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class RedirectingController
{

    private final RedirectingService service;
    RedirectingController(RedirectingService service)
    {
        this.service=service;
    }

    // This is used by analytics service
    @GetMapping("/internal/clicks/{shortCode}")
    public ClickCountResponse getClicks(
            @PathVariable String shortCode)
    {
        return new ClickCountResponse(
                service.getClickCount(shortCode)
        );
    }





    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode)
    {
        String originalUrl =
                service.getOriginalUrl(shortCode);

        service.updateCount(shortCode);

        HttpHeaders headers =
                new HttpHeaders();

        headers.setLocation(
                URI.create(originalUrl)
        );

        return new ResponseEntity<>(
                headers,
                HttpStatus.FOUND
        );
    }
}

