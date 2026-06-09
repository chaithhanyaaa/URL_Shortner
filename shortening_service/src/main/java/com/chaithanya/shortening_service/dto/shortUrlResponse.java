package com.chaithanya.shortening_service.dto;

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