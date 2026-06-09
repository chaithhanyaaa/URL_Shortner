package com.chaithanya.analytics_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AnalyticsResponse
{
    private String shortCode;
    private String originalUrl;
    private Long clickCount;
}
