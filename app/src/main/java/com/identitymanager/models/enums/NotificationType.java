package com.identitymanager.models.enums;

public enum NotificationType {

    PASSWORD_CHANGE (0);

    private int value;

    NotificationType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
