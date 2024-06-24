package com.cache.redis.service;

import com.cache.entity.NewspaperFiles;
import com.cache.entity.UserSubscription;
import com.cache.model.RedisCacheObject;
import com.cache.repository.NewspaperFilesRepository;
import com.cache.repository.UserSubscriptionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RedisCacheService {

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    @Autowired
    private NewspaperFilesRepository newspaperFileRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean createAndCacheBatchData(Long batchId, String stateName, String language) {
        List<UserSubscription> subscriptions = getValidUserSubscriptions(batchId, stateName, language);
        List<NewspaperFiles> files = getTodaysNewspaperFiles();

        if (subscriptions.isEmpty()) {
            return false;
        }

        Map<String, RedisCacheObject> cacheObjects = createRedisCacheObjects(subscriptions, files);
        return cacheRedisObjects(cacheObjects);
    }

    private List<UserSubscription> getValidUserSubscriptions(Long batchId, String stateName, String language) {
        return userSubscriptionRepository.findAll().stream()
                .filter(sub -> sub.getSubscriptionStartDate().before(new Date()) 
                    && sub.getSubscriptionEndDate().after(new Date())
                    && (batchId == null || sub.getBatch().getBatchId().equals(batchId))
                    && (stateName == null || sub.getVendor().getLocation().getState().getStateName().equals(stateName))
                    && (language == null || sub.getVendor().getNewspaperLanguage().getLanguageName().equals(language)))
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

    @SuppressWarnings("unused")
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

    private boolean cacheRedisObjects(Map<String, RedisCacheObject> cacheObjects) {
        if (cacheObjects.isEmpty()) {
            return false;
        }

        boolean success = true;
        for (Map.Entry<String, RedisCacheObject> entry : cacheObjects.entrySet()) {
            try {
                String jsonString = new ObjectMapper().writeValueAsString(entry.getValue());
                redisTemplate.opsForValue().set(entry.getKey(), jsonString);
                redisTemplate.expire(entry.getKey(), 60, TimeUnit.MINUTES);
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
        }
        return success;
    }
    
    public void removeCacheByKey(String key) {
        redisTemplate.delete(key);
    }
}
