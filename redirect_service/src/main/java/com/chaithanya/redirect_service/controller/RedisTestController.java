package com.chaithanya.redirect_service.controller;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class RedisTestController {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisTestController(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/set")
    public String setValue() {

        redisTemplate.opsForValue().set("abc", "https://google.com");

        return "saved";
    }

    @GetMapping("/get")
    public String getValue() {

        return redisTemplate.opsForValue()
                .get("abc");
    }
}
