package com.cache.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "MASTER_MANDALS")
@Data
public class Mandal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mandal_id")
	private Long mandalId;

	@Column(name = "mandal_name")
	private String mandalName;

	@ManyToOne
	@JoinColumn(name = "district_id", referencedColumnName = "district_id")
	private District district;
}
