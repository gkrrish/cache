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
@Table(name = "MASTER_NEWS_LANGUAGES")
@Data
public class IndianNewspaperLanguage {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "language_seq_gen")
	@SequenceGenerator(name = "language_seq_gen", sequenceName = "LANGUAGE_SEQ", allocationSize = 1)
	@Column(name = "language_id")
	private int languageId;
	private String languageName;
}
