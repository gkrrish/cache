package com.cache.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RedisCacheObject {

    private Long batchTime;
    private List<UserInfo> users;
    private List<NewspaperInfo> newspapers;

    public void setBatchTime(Long batchTime) {
        this.batchTime = batchTime;
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
        private String email;

        public UserInfo(Long userId, String email) {
            this.userId = userId;
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
