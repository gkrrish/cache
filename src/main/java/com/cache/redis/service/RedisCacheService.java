package com.cache.redis.service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.cache.entity.NewspaperFiles;
import com.cache.entity.UserSubscription;
import com.cache.model.RedisCacheObject;
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

    public void createAndCacheBatchData() {
        List<UserSubscription> subscriptions = getValidUserSubscriptions();
        List<NewspaperFiles> files = getTodaysNewspaperFiles();

        RedisCacheObject cacheObject = createRedisCacheObject(subscriptions, files);
        cacheRedisObject(cacheObject);
    }

    private List<UserSubscription> getValidUserSubscriptions() {
        // Fetch valid subscriptions
        return userSubscriptionRepository.findAll().stream()
                .filter(sub -> sub.getSubscriptionStartDate().before(new Date()) && sub.getSubscriptionEndDate().after(new Date()))
                .collect(Collectors.toList());
    }

    private List<NewspaperFiles> getTodaysNewspaperFiles() {
        // Fetch today's newspaper files
        return newspaperFileRepository.findAll().stream()
                .filter(file -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    Date today = cal.getTime();
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    Date tomorrow = cal.getTime();
                    return !file.getUploadDate().before(today) && file.getUploadDate().before(tomorrow);
                })
                .collect(Collectors.toList());
    }

    private RedisCacheObject createRedisCacheObject(List<UserSubscription> subscriptions, List<NewspaperFiles> files) {
        RedisCacheObject cacheObject = new RedisCacheObject();

        if (!subscriptions.isEmpty()) {
            cacheObject.setBatchTime(subscriptions.get(0).getBatch().getBatchId());
        }

        cacheObject.setUsers(subscriptions.stream()
                .map(sub -> new RedisCacheObject.UserInfo(sub.getUserDetails().getUserid(), sub.getUserDetails().getUsername()))
                .collect(Collectors.toList()));

        Map<Long, List<UserSubscription>> newspaperGroups = subscriptions.stream()
                .collect(Collectors.groupingBy(sub -> sub.getVendor().getId().getNewspaperId()));

        cacheObject.setNewspapers(newspaperGroups.entrySet().stream().map(entry -> {
            Long newspaperId = entry.getKey();
            List<UserSubscription> subs = entry.getValue();
            Map<String, List<Long>> fileLocations = files.stream()
                    .filter(file -> file.getVendor().getId().getNewspaperId().equals(newspaperId))
                    .collect(Collectors.groupingBy(NewspaperFiles::getFileLocation,
                            Collectors.mapping(file -> file.getVendor().getLocation().getLocationId(), Collectors.toList())));
            RedisCacheObject.NewspaperInfo newspaperInfo = new RedisCacheObject.NewspaperInfo();
            newspaperInfo.setNewspaperId(newspaperId);
            newspaperInfo.setFileLocations(fileLocations);
            return newspaperInfo;
        }).collect(Collectors.toList()));

        return cacheObject;
    }

    private void cacheRedisObject(RedisCacheObject cacheObject) {
        String redisKey = "batch:" + cacheObject.getBatchTime();
        try {
            String jsonString = new ObjectMapper().writeValueAsString(cacheObject);
            redisTemplate.opsForValue().set(redisKey, jsonString);
            redisTemplate.expire(redisKey, 60, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
