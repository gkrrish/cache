package com.cache.redis.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poc.auser.main.entity.NewspaperFiles;
import com.poc.auser.main.repository.NewspaperFilesRepository;
import com.poc.auser.master.entity.IndianNewspaperLanguage;
import com.poc.auser.master.entity.Vendor;
import com.poc.auser.master.repository.IndianNewspaperLanguageRepository;
import com.poc.auser.master.repository.VendorRepository;

import jakarta.annotation.PostConstruct;

@Service
public class NewspaperCacheService {

    @Autowired
    private NewspaperFilesRepository newspaperFileRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private IndianNewspaperLanguageRepository newsLanguageRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisScript<Boolean> manageCacheScript;

    @PostConstruct
    @Transactional(readOnly = true)
    public void loadNewspapersIntoCache() {
        List<NewspaperFiles> newspaperFiles = newspaperFileRepository.findAll();

        for (NewspaperFiles newspaperFile : newspaperFiles) {
            Long newspaperId = newspaperFile.getVendor().getId().getNewspaperId();
            List<Vendor> vendors = vendorRepository.findByNewspaperId(newspaperId);

            for (Vendor vendor : vendors) {
                Long languageId = Long.valueOf(vendor.getNewspaperLanguage().getLanguageId());
                IndianNewspaperLanguage newsLanguage = newsLanguageRepository.findById(languageId.intValue()).orElse(null);

                if (newsLanguage != null) {
                    String language = newsLanguage.getLanguageName();
                    String locationId = newspaperFile.getVendor().getId().getLocationId().toString();
                    String fileLocation = newspaperFile.getFileLocation();

                    redisTemplate.execute(manageCacheScript, List.of(), language, locationId, fileLocation);
                }
            }
        }
    }
}
