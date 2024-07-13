package com.cache.redis.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.cache.entity.NewspaperFiles;
import com.cache.entity.UserSubscription;
import com.cache.exceptions.ActiveUsersNotFoundException;
import com.cache.exceptions.RedisDataRetrivingException;
import com.cache.exceptions.TodayNewspaperNotPresentException;
import com.cache.model.RedisCacheObject;
import com.cache.model.RedisCacheObject.NewspaperInfo;
import com.cache.repository.NewspaperFilesRepository;
import com.cache.repository.UserSubscriptionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RedisCacheService {
	private static final Logger logger = LoggerFactory.getLogger(RedisCacheService.class);

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    @Autowired
    private NewspaperFilesRepository newspaperFileRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

	/**
	 * 
	 * Creates the Cache Data
	 * 
	 * We are fetching a large amount of user subscription data (means entire Relational data record)from the database and then convert it to RedisCacheObject, which is lightweight. However, 
	 * the initial data is bulky. To reduce the size, I considered keeping only the necessary fields in RedisCacheObject. But, since targeted advertisements and other
	 * validations might need additional fields later, I decided to keep the full data for now and review the required fields later
	 */
    
    public boolean createCacheData(Long batchId, String stateName, String language) {  
    	logger.info("Creating the Cache Data for Batch Id : {}, State Name : {} , Languge : {} ", batchId, stateName, language);
    	
        List<UserSubscription> subscriptions = getActiveSubscribers(batchId, stateName, language);
        List<NewspaperInfo> uniqueFiles = getTodayNewspaperFiles(stateName, language);

        if (subscriptions.isEmpty() || uniqueFiles.isEmpty()) {
        	logger.info("The data is empty one of among Active Subscription or Unique Newspapers. ");
            return false;
        }

        RedisCacheObject cacheObject = createRedisCacheObject(batchId, stateName, language, subscriptions, uniqueFiles);
        return cacheRedisObject(cacheObject);
    }
    
    /**
     *  List of UserSubscription of Active Subscribers
     */
    private List<UserSubscription> getActiveSubscribers(Long batchId, String stateName, String language) {
    	List<UserSubscription> validUserSubscriptions = userSubscriptionRepository.findValidUserSubscriptions(batchId, stateName, language);
    	
    	if(validUserSubscriptions==null || validUserSubscriptions.isEmpty()) {
    		throw new ActiveUsersNotFoundException("No Active Users found in User-Subscription, might be a Subscription Dates Ended?");
    	}
        return validUserSubscriptions;
    }

    /**
     * gives the Today newspaper Files, if today news papers are not available hence throws an exception
     */
    private List<NewspaperInfo> getTodayNewspaperFiles(String stateName, String languageName) {
    	List<NewspaperFiles> todaysNewspaperFiles = newspaperFileRepository.findTodaysNewspaperFiles(stateName, languageName);

        if (todaysNewspaperFiles == null || todaysNewspaperFiles.isEmpty()) {
            throw new TodayNewspaperNotPresentException("Looks like today's newspapers aren't updated.");
        }
        return getUniqueNewspaperFiles(todaysNewspaperFiles);
    }
    
    /**
     * Generally we gets the 
     */
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
		
		if (data == null) return null;

		try {
			return new ObjectMapper().readValue(data, RedisCacheObject.class);
		} catch (Exception e) {
			throw new RedisDataRetrivingException("Got an error while getDataFromCache :" + e.getMessage());
		}
	}

    public void removeCacheByKey(String key) {
        redisTemplate.delete(key);
    }
}
