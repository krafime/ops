package com.dansmultipro.ops.util;

import java.util.UUID;

public class UUIDUtil {

    public static UUID toUUID(String uuidString) {
        if (uuidString == null || uuidString.isEmpty()) {
            throw new IllegalArgumentException("UUID string cannot be null or empty");
        }
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format: " + uuidString);
        }
    }

    public static String toString(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return uuid.toString();
    }
}

