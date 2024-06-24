package com.cache.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "VENDOR_DETAILS")
@Data
public class VendorDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vendor_details_seq")
	@SequenceGenerator(name = "vendor_details_seq", sequenceName = "VENDOR_DETAILS_SEQ", allocationSize = 1)
	@Column(name = "vendorid")
	private int vendorId;

	@Column(name = "vendorname", length = 255)
	private String vendorName;

	@Column(name = "vendorcontactdetails", length = 512)
	private String vendorContactDetails;

	@Column(name = "vendorstatus", length = 10)
	private String vendorStatus;

}
