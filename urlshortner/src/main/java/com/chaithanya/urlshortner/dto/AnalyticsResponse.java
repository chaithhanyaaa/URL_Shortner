package com.chaithanya.urlshortner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse
{
    private String shortCode;
    private String originalUrl;
    private Long clickCount;
}
