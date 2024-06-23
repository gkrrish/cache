package com.cache.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cache.redis.service.NewspaperCacheService;

@RestController
@RequestMapping("/api/cache")
public class CacheController {

    @Autowired
    private NewspaperCacheService newspaperCacheService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/load")
    public String loadCache() {
        newspaperCacheService.loadNewspapersIntoCache();
        return "Cache loaded successfully";
    }

    @GetMapping("/retrieve")
    public Object retrieveCache() {
        return redisTemplate.keys("newspapers:*");
    }
}
