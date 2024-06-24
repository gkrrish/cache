package com.cache.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SubscriptionDurationEnumConverter implements AttributeConverter<SubscriptionType.SubscriptionDurationEnum, String> {

    @Override
    public String convertToDatabaseColumn(SubscriptionType.SubscriptionDurationEnum attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDuration();
    }

    @Override
    public SubscriptionType.SubscriptionDurationEnum convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return SubscriptionType.SubscriptionDurationEnum.fromDuration(dbData);
    }
}