package com.dansmultipro.ops.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Component
public class CurrencyFormatter {

    public String formatRupiah(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("id", "ID"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');

        DecimalFormat formatter = new DecimalFormat("#,##0", symbols);
        return formatter.format(amount.longValue());
    }
}

