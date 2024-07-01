package com.cache.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cache.entity.UserSubscription;
import com.cache.entity.UserSubscriptionId;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, UserSubscriptionId> {
	
	/**
	 * 
	 * @param batchId
	 * @param stateName
	 * @param language
	 * @return UserSubscription
	 * batchId is mandatory and stateName, Language are optional, checking with CurrentDate (today) for active subscriptions.
	 */
	@Query("SELECT us FROM UserSubscription us " +
	           "JOIN us.batch b " +
	           "JOIN us.vendor v " +
	           "LEFT JOIN v.location loc " +
	           "LEFT JOIN loc.state s " +
	           "LEFT JOIN v.newspaperLanguage lg " +
	           "WHERE us.subscriptionStartDate < CURRENT_DATE " +
	           "AND us.subscriptionEndDate > CURRENT_DATE " +
	           "AND us.batch.batchId = :batchId " +
	           "AND (:stateName IS NULL OR s.stateName = :stateName) " +
	           "AND (:language IS NULL OR lg.languageName = :language)")
	    List<UserSubscription> findValidUserSubscriptions(
	            @Param("batchId") Long batchId,
	            @Param("stateName") String stateName,
	            @Param("language") String language);
}

//mostly newspaper id has unique mandal id, and mandal id is nothing but location id, vendor and user_subscription tables are important to understand the constrains and flow of the application.
//end of completion of application write a neat notes for database relations so next time understanding will be easy