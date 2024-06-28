package com.cache.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RedisCacheObject {

    private Long batchTime;
    private String state;
    private String language;
    private List<UserInfo> users;
    private List<NewspaperInfo> newspapers;

    public void setBatchTime(Long batchTime) {
        this.batchTime = batchTime;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setUsers(List<UserInfo> users) {
        this.users = users;
    }

    public void setNewspapers(List<NewspaperInfo> newspapers) {
        this.newspapers = newspapers;
    }

    @Data
    public static class UserInfo {
        private Long userId;
        private String userMobileNumber;
        private String email;

        public UserInfo(Long userId,String userMobileNumber, String email) {
            this.userId = userId;
            this.userMobileNumber=userMobileNumber;
            this.email = email;
        }
    }

    @Data
    public static class NewspaperInfo {
        private Long newspaperId;
        private Map<String, List<Long>> fileLocations;

        public void setNewspaperId(Long newspaperId) {
            this.newspaperId = newspaperId;
        }

        public void setFileLocations(Map<String, List<Long>> fileLocations) {
            this.fileLocations = fileLocations;
        }
    }
}
