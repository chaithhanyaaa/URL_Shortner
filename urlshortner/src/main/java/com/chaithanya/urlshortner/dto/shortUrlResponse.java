package com.chaithanya.urlshortner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class shortUrlResponse {

    private String shortUrl;
    public shortUrlResponse()
    {
        shortUrl=null;
    }
}