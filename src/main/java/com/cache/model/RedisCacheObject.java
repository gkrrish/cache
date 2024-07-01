package com.cache.model;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonSerialize
@JsonDeserialize
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
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode
	public static class UserInfo {
		private Long userId;
		private String userMobileNumber;
		private String email;
		private Long newspaperId; // matches with associateNewspaperIds
	}

	@Data
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode
	public static class NewspaperInfo {
		private Long newspaperFileId;
		private String newsPaperfileName;
		private List<Long> associateNewspaperIds;
		
	}
}
