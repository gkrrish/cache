package com.cache.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "VENDORS")
@Data
public class Vendor {

    @EmbeddedId
    private VendorId id;

    @ManyToOne
    @MapsId("locationId")
    @JoinColumn(name = "location_id", referencedColumnName = "location_id")
    private StatewiseLocation location;

    @ManyToOne
    @MapsId("newspaperMasterId")
    @JoinColumn(name = "newspaper_master_id", referencedColumnName = "newspaper_master_id")
    private MasterNewspaper newspaperMaster;

    @ManyToOne
    @JoinColumn(name = "newspaper_language", referencedColumnName = "language_id")
    private IndianNewspaperLanguage newspaperLanguage;

    @ManyToOne
    @JoinColumn(name = "subscription_type_id", referencedColumnName = "subscriptiontypeid")
    private SubscriptionType subscriptionType;

    @Column(name = "publication_type", length = 10)
    private String publicationType;

    @Column(name = "category_id")
    private Long categoryId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id", insertable = false, updatable = false)
    private CategoryType category;
}
