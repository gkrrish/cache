package com.cache.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Table(name = "USER_DETAILS")
@Data
public class UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
	@SequenceGenerator(name = "user_seq_gen", sequenceName = "USER_SEQ", allocationSize = 1)
	private Long userid;

	@Column(name = "mobilenumber", nullable = false, length = 13, unique = true)
	@NotNull
	private String mobileNumber;

	@Column(name = "username", nullable = false, length = 50)
	private String username;

	@Column(name = "age")
	private Integer age;

	@Column(name = "gender", nullable = false, length = 10)
	@Pattern(regexp = "^(Male|Female|Other)$")
	private String gender;

	@Column(name = "location", length = 100)
	private String location;

	@Column(name = "registrationdate")
	private Timestamp registrationDate;

	@Column(name = "active", nullable = false, length = 1)
	@Pattern(regexp = "^[YN]$")
	private Character active;

}
