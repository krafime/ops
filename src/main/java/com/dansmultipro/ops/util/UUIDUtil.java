package com.dansmultipro.ops.util;

import java.util.UUID;

public class UUIDUtil {

    /**
     * Convert String to UUID
     *
     * @param uuidString String representation of UUID
     * @return UUID object
     * @throws IllegalArgumentException if string is not valid UUID format
     */
    public static UUID toUUID(String uuidString) {
        if (uuidString == null || uuidString.isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format: " + uuidString);
        }
    }
}

