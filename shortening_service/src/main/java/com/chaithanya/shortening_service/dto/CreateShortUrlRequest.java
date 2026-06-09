package com.chaithanya.shortening_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateShortUrlRequest {

    @NotBlank
    private String url;
}
