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
@Table(name = "MASTER_STATES")
@Data
public class State {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "state_seq_gen")
	@SequenceGenerator(name = "state_seq_gen", sequenceName = "STATE_SEQ", allocationSize = 1)
	@Column(name = "state_id")
	private Integer stateId;

	@Column(name = "state_name")
	private String stateName;

	@Column(name = "country_id")
	private int countryId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "country_id", referencedColumnName = "country_id", insertable = false, updatable = false)
	private Country country;
}
