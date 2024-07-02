package com.cache.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cache.entity.NewspaperFiles;

@Repository
public interface NewspaperFilesRepository extends JpaRepository<NewspaperFiles, Long> {
	
	@Query(value = "SELECT nf.* FROM NEWSPAPER_FILES nf " +
            "JOIN VENDORS v ON nf.newspaper_id = v.newspaper_id " +
            "AND nf.location_id = v.location_id " +
            "AND nf.newspaper_master_id = v.newspaper_master_id " +
            "JOIN MASTER_STATEWISE_LOCATIONS sl ON v.location_id = sl.location_id " +
            "JOIN MASTER_STATES ms ON sl.state_id = ms.state_id " +
            "JOIN MASTER_NEWS_LANGUAGES nl ON v.newspaper_language = nl.language_id " +
            "WHERE nf.upload_date >= TRUNC(CURRENT_DATE) " +
            "AND nf.upload_date < TRUNC(CURRENT_DATE + 1) " +
            "AND (ms.state_name = :stateName OR :stateName IS NULL) " +
            "AND (nl.language_name = :languageName OR :languageName IS NULL)", 
    nativeQuery = true)
	List<NewspaperFiles> findTodaysNewspaperFiles(@Param("stateName") String stateName, @Param("languageName") String languageName);
	//be-cautious the date should be today in database
}
