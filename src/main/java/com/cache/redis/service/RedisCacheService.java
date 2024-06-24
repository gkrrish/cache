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

        Map<String, RedisCacheObject> cacheObjects = createRedisCacheObjects(subscriptions, files);
        cacheRedisObjects(cacheObjects);
    }

    private List<UserSubscription> getValidUserSubscriptions() {
        return userSubscriptionRepository.findAll().stream()
                .filter(sub -> sub.getSubscriptionStartDate().before(new Date()) && sub.getSubscriptionEndDate().after(new Date()))
                .collect(Collectors.toList());
    }

    private List<NewspaperFiles> getTodaysNewspaperFiles() {
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

    private Map<String, RedisCacheObject> createRedisCacheObjects(List<UserSubscription> subscriptions, List<NewspaperFiles> files) {
        Map<String, RedisCacheObject> cacheObjects = new HashMap<>();

        Map<Long, Map<String, Map<String, List<UserSubscription>>>> groupedSubscriptions = subscriptions.stream()
                .collect(Collectors.groupingBy(
                        sub -> sub.getBatch().getBatchId(),
                        Collectors.groupingBy(
                                sub -> sub.getVendor().getLocation().getState().getStateName(),
                                Collectors.groupingBy(sub -> sub.getVendor().getNewspaperLanguage().getLanguageName())
                        )
                ));

        groupedSubscriptions.forEach((batchTime, stateMap) -> {
            stateMap.forEach((state, languageMap) -> {
                languageMap.forEach((language, subs) -> {
                    RedisCacheObject cacheObject = new RedisCacheObject();
                    cacheObject.setBatchTime(batchTime);
                    cacheObject.setState(state);
                    cacheObject.setLanguage(language);

                    cacheObject.setUsers(subs.stream()
                            .map(sub -> new RedisCacheObject.UserInfo(sub.getUserDetails().getUserid(), sub.getUserDetails().getUsername()))
                            .collect(Collectors.toList()));

                    Map<Long, List<UserSubscription>> newspaperGroups = subs.stream()
                            .collect(Collectors.groupingBy(sub -> sub.getVendor().getId().getNewspaperId()));

                    cacheObject.setNewspapers(newspaperGroups.entrySet().stream().map(entry -> {
                        Long newspaperId = entry.getKey();
                        List<UserSubscription> groupSubs = entry.getValue();
                        Map<String, List<Long>> fileLocations = files.stream()
                                .filter(file -> file.getVendor().getId().getNewspaperId().equals(newspaperId))
                                .collect(Collectors.groupingBy(NewspaperFiles::getFileLocation,
                                        Collectors.mapping(file -> file.getVendor().getLocation().getLocationId(), Collectors.toList())));
                        RedisCacheObject.NewspaperInfo newspaperInfo = new RedisCacheObject.NewspaperInfo();
                        newspaperInfo.setNewspaperId(newspaperId);
                        newspaperInfo.setFileLocations(fileLocations);
                        return newspaperInfo;
                    }).collect(Collectors.toList()));

                    String key = "batch:" + batchTime + ":state:" + state + ":language:" + language;
                    cacheObjects.put(key, cacheObject);
                });
            });
        });

        return cacheObjects;
    }

    private void cacheRedisObjects(Map<String, RedisCacheObject> cacheObjects) {
        cacheObjects.forEach((key, cacheObject) -> {
            try {
                String jsonString = new ObjectMapper().writeValueAsString(cacheObject);
                redisTemplate.opsForValue().set(key, jsonString);
                redisTemplate.expire(key, 60, TimeUnit.MINUTES);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
