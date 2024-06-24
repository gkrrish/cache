package com.cache.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "MASTER_BATCH_JOBS")
@Data
public class BatchJob {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "BATCH_ID")
	private Long batchId;

	@Column(name = "DELIVERY_TIME")
	private String deliveryTime;

	@Column(name = "INTERVAL_MINUTES", columnDefinition = "NUMBER(2) DEFAULT 30")
	private Integer intervalMinutes;
}