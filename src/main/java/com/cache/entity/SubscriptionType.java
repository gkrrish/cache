package com.cache.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SUBSCRIPTION_TYPE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionType {

    public enum SubscriptionDurationEnum {
        ONE_MONTH("1 MONTH"),
        THREE_MONTHS("3 MONTHS"),
        SIX_MONTHS("6 MONTHS"),
        TWELVE_MONTHS("12 MONTHS");

        private String duration;

        SubscriptionDurationEnum(String duration) {
            this.duration = duration;
        }

        public String getDuration() {
            return duration;
        }
     // Add a method to get enum constant by duration string
        public static SubscriptionDurationEnum fromDuration(String duration) {
            for (SubscriptionDurationEnum value : values()) {
                if (value.duration.equals(duration)) {
                    return value;
                }
            }
            throw new IllegalArgumentException("Invalid Subscription Duration: " + duration);
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscriptiontypeid")  
    private Long subscriptionTypeId;
    
    @Convert(converter = SubscriptionDurationEnumConverter.class)
    @Column(name = "subscriptionduration")
    private SubscriptionDurationEnum subscriptionDuration;

    @Column(name = "subscriptionfee")  
    private double subscriptionFee;

    @Column(name = "subscriptiontype")  
    private String subscriptionType;
}
