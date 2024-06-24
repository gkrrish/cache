package com.cache.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorId implements Serializable {
  
    private static final long serialVersionUID = 1L;

    @Column(name = "newspaper_id")
    private Long newspaperId;

    @Column(name = "location_id")
    private Long locationId;

    @Column(name = "newspaper_master_id")
    private Long newspaperMasterId;
}
