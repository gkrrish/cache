package com.cache.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cache.redis.service.RedisCacheService;

@RestController
@RequestMapping("/cache")
public class CacheControllerr {
	
	@Autowired
    private RedisCacheService redisCacheService;

    @GetMapping("/update")
    public String updateCache() {
        redisCacheService.createAndCacheBatchData();
        return "Cache Updated!";
    }

}
