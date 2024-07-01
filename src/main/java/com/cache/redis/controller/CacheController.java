package com.cache.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cache.model.RedisCacheObject;
import com.cache.redis.service.RedisCacheService;

@RestController
@RequestMapping("/cache")
public class CacheController {
	
	@Autowired
    private RedisCacheService redisCacheService;

    @GetMapping("/update/batch/{batchId}")
    public String updateCacheByBatchId(@PathVariable Long batchId) {
        boolean success = redisCacheService.createCacheData(batchId, null, null);
        return success ? "Cache Updated for batchId: " + batchId : "Failed to update cache for batchId: " + batchId;
    }

    @GetMapping("/update/batch/{batchId}/state/{stateName}")
    public String updateCacheByBatchIdAndState(@PathVariable Long batchId, @PathVariable String stateName) {
        boolean success = redisCacheService.createCacheData(batchId, stateName, null);
        return success ? "Cache Updated for batchId: " + batchId + " and state: " + stateName : "Failed to update cache for batchId: " + batchId + " and state: " + stateName;
    }//some of controllers are not needed, if multiple state or single state muliple languages there means how the RedisCacheObject handles?

    @GetMapping("/update/batch/{batchId}/state/{stateName}/language/{language}")
    public String updateCacheByBatchIdStateAndLanguage(@PathVariable Long batchId, @PathVariable String stateName, @PathVariable String language) {
        boolean success = redisCacheService.createCacheData(batchId, stateName, language);
        return success ? "Cache Updated for batchId: " + batchId + ", state: " + stateName + " and language: " + language : "Failed to update cache for batchId: " + batchId + ", state: " + stateName + " and language: " + language;
    }
    
    @GetMapping("/get/{key}")
    public RedisCacheObject getDataFromCache(@PathVariable String key) {
        return redisCacheService.getDataFromCache(key);
    }
}
