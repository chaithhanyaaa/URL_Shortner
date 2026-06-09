package com.chaithanya.shortening_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OriginalUrlResponse
{
    private String originalUrl;
}
//used to send to redirectservice