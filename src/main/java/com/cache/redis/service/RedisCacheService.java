package com.cache.redis.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.cache.entity.NewspaperFiles;
import com.cache.entity.UserSubscription;
import com.cache.model.RedisCacheObject;
import com.cache.model.RedisCacheObject.NewspaperInfo;
import com.cache.repository.NewspaperFilesRepository;
import com.cache.repository.UserSubscriptionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RedisCacheService {

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    @Autowired
    private NewspaperFilesRepository newspaperFileRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean createCacheData(Long batchId, String stateName, String language) {  
        List<UserSubscription> subscriptions = getActiveSubscribers(batchId, stateName, language);
        List<NewspaperInfo> uniqueFiles = getTodayNewspaperFiles(stateName, language);

        if (subscriptions.isEmpty() || uniqueFiles.isEmpty()) {
            return false;
        }

        RedisCacheObject cacheObject = createRedisCacheObject(batchId, stateName, language, subscriptions, uniqueFiles);
        return cacheRedisObject(cacheObject);
    }
    
    private List<UserSubscription> getActiveSubscribers(Long batchId, String stateName, String language) {
        return userSubscriptionRepository.findValidUserSubscriptions(batchId, stateName, language);
    }

    private List<NewspaperInfo> getTodayNewspaperFiles(String stateName, String languageName) {
    	List<NewspaperFiles> todaysNewspaperFiles = newspaperFileRepository.findTodaysNewspaperFiles(stateName, languageName);
    	List<NewspaperInfo> uniqueNewspaperFiles = null;
    	
    	if(todaysNewspaperFiles!=null) {
    	 uniqueNewspaperFiles = getUniqueNewspaperFiles(todaysNewspaperFiles);
    	}
        return uniqueNewspaperFiles;
    }
    
    public List<NewspaperInfo> getUniqueNewspaperFiles(List<NewspaperFiles> todaysNewspaperFiles) {
        Map<String, NewspaperInfo> fileMap = new HashMap<>();

        for (NewspaperFiles file : todaysNewspaperFiles) {
            String fileLocation = file.getFileLocation();
            Long newspaperId = file.getVendor().getId().getNewspaperId();

            if (!fileMap.containsKey(fileLocation)) {
                NewspaperInfo info = new NewspaperInfo();
                info.setNewspaperFileId(file.getFileId());
                info.setNewsPaperfileName(fileLocation);
                info.setAssociateNewspaperIds(new ArrayList<>());
                fileMap.put(fileLocation, info);
            }
            
            fileMap.get(fileLocation).getAssociateNewspaperIds().add(newspaperId);
        }

        return new ArrayList<>(fileMap.values());
    }
    
    private RedisCacheObject createRedisCacheObject(Long batchTime, String state, String language, List<UserSubscription> subscriptions, List<NewspaperInfo> uniqueFiles) {
        RedisCacheObject cacheObject = new RedisCacheObject();
        cacheObject.setBatchTime(batchTime);
        if (!subscriptions.isEmpty()) {
            cacheObject.setState(subscriptions.get(0).getVendor().getLocation().getState().getStateName());
            cacheObject.setLanguage(subscriptions.get(0).getVendor().getNewspaperLanguage().getLanguageName());
        }

        cacheObject.setUsers(subscriptions.stream()
            .map(sub -> new RedisCacheObject.UserInfo(
            								sub.getUserDetails().getUserid(), 
            								sub.getUserDetails().getMobileNumber(), 
            								sub.getUserDetails().getUsername(), 
            								sub.getVendor().getId().getNewspaperId())) 
            								.collect(Collectors.toList()));

        cacheObject.setNewspapers(uniqueFiles);

        return cacheObject;
    }
    
    private boolean cacheRedisObject(RedisCacheObject cacheObject) {
        if (cacheObject == null) return false;
        boolean success = true;
        try {
            String key = "batch:" + cacheObject.getBatchTime() + ":state:" + cacheObject.getState() + ":language:" + cacheObject.getLanguage();
            String jsonString = new ObjectMapper().writeValueAsString(cacheObject);

            redisTemplate.opsForValue().setIfAbsent(key, jsonString, 60, TimeUnit.MINUTES);
            
        } catch (Exception e) {
            e.getMessage();
            success = false;
        }
        return success;
    }

    public RedisCacheObject getDataFromCache(String key) {
        String data = redisTemplate.opsForValue().get(key);
        if (data != null) {
            try {
                return new ObjectMapper().readValue(data, RedisCacheObject.class);
            } catch (Exception e) {
                throw new RuntimeException("Got an error while getDataFromCache :"+e.getMessage());
            }
        }
        return null;
    }

    public void removeCacheByKey(String key) {
        redisTemplate.delete(key);
    }
}
