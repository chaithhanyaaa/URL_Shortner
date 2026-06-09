package com.chaithanya.shortening_service.exception;

public class UrlNotFoundException extends RuntimeException
{
    public UrlNotFoundException(String message)
    {
        super(message);
    }
}
