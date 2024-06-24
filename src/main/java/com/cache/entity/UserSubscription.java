package com.cache.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USER_SUBSCRIPTION")
@Data
@NoArgsConstructor
public class UserSubscription {

    @EmbeddedId
    private UserSubscriptionId id;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserDetails userDetails;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "newspaper_id", referencedColumnName = "newspaper_id", insertable = false, updatable = false),
        @JoinColumn(name = "location_id", referencedColumnName = "location_id", insertable = false, updatable = false),
        @JoinColumn(name = "newspaper_master_id", referencedColumnName = "newspaper_master_id", insertable = false, updatable = false)
    })
    private Vendor vendor;
    
    @Column(name = "subscription_start_date")
    private Date subscriptionStartDate;

    @Column(name = "subscription_end_date")
    private Date subscriptionEndDate;

    @ManyToOne
    @JoinColumn(name = "batch_id")
    private BatchJob batch;
    
    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
        if (this.id == null) {
            this.id = new UserSubscriptionId();
        }
        this.id.setUser(userDetails);
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
        if (this.id == null) {
            this.id = new UserSubscriptionId();
        }
        this.id.setVendor(vendor);
    }
}
