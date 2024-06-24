package com.cache.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "MASTER_DISTRICTS")
@Data
public class District {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "district_seq_gen")
	@SequenceGenerator(name = "district_seq_gen", sequenceName = "DISTRICT_SEQ", allocationSize = 1)
	@Column(name = "district_id")
	private Integer districtId;

	@Column(name = "district_name", length = 100)
	private String districtName;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "state_id")
	private State state;
}