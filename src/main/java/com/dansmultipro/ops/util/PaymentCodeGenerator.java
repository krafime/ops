package com.dansmultipro.ops.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class PaymentCodeGenerator {

    private PaymentCodeGenerator() {
        // Utility class
    }

    /**
     * Generate payment code with format: PAY-yyyyMMddHHmmss-XXXX
     * Example: PAY-20251110145000-A1B2
     */
    public static String generatePaymentCode() {
        var timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        var randomSuffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "PAY-" + timestamp + "-" + randomSuffix;
    }
}

