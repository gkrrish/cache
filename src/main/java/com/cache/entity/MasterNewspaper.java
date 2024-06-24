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
@Table(name = "MASTER_NEWSPAPER")
@Data
public class MasterNewspaper {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "newspaper_master_id")
	private Long id;

	@Column(name = "newspaper_name", length = 100, unique = true)
	private String newspaperName;

	@ManyToOne
	@JoinColumn(name = "vendor_id", referencedColumnName = "vendorid")
	private VendorDetails vendor;
}
