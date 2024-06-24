package com.cache.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class UserSubscriptionId implements Serializable {
    private static final long serialVersionUID = 3838752332485857554L;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDetails user;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "newspaper_id", referencedColumnName = "newspaper_id"),
        @JoinColumn(name = "location_id", referencedColumnName = "location_id"),
        @JoinColumn(name = "newspaper_master_id", referencedColumnName = "newspaper_master_id")
    })
    private Vendor vendor;
    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSubscriptionId that = (UserSubscriptionId) o;
        return Objects.equals(user, that.user) &&
               Objects.equals(vendor, that.vendor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, vendor);
    }
}
