package com.cache.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "MASTER_STATEWISE_LOCATIONS")
@Data
public class StatewiseLocation {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_seq_gen")
	@SequenceGenerator(name = "location_seq_gen", sequenceName = "LOCATION_SEQ", allocationSize = 1)
	@Column(name = "location_id")
	private Long locationId;

	@Column(name = "location_name")
	private String locationName;

	@ManyToOne
	@JoinColumn(name = "country_id", referencedColumnName = "country_id")
	private Country country;

	@ManyToOne
	@JoinColumn(name = "state_id", referencedColumnName = "state_id")
	private State state;

	@ManyToOne
	@JoinColumn(name = "district_id", referencedColumnName = "district_id")
	private District district;

	@ManyToOne
	@JoinColumn(name = "mandal_id", referencedColumnName = "mandal_id")
	private Mandal mandal;
}