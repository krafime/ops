package com.dansmultipro.ops.util;

import com.dansmultipro.ops.constant.PaymentStatusConstant;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class EmailMessageBuilder {

    private final CurrencyFormatter currencyFormatter;
    private final TemplateEngine templateEngine;
    private final DateTimeUtil dateTimeUtil;

    public EmailMessageBuilder(CurrencyFormatter currencyFormatter, TemplateEngine templateEngine, DateTimeUtil dateTimeUtil) {
        this.dateTimeUtil = dateTimeUtil;
        this.currencyFormatter = currencyFormatter;
        this.templateEngine = templateEngine;
    }

    public String buildPaymentHtml(
            String customerName,
            String paymentCode,
            String customerCode,
            BigDecimal paymentFee,
            String paymentType,
            String productType,
            BigDecimal amount,
            String status
    ) {
        String intro, statusText, statusColor;

        if (PaymentStatusConstant.SUCCESS.name().equalsIgnoreCase(status)) {
            intro = "Selamat! Pembayaran Anda telah <b>berhasil</b> diproses. Terima kasih telah melakukan pembayaran.";
            statusText = "BERHASIL";
            statusColor = "#0A7C2F";
        } else {
            intro = "Pembayaran Anda telah <b>ditolak</b>. Silakan coba lagi atau hubungi customer service kami untuk bantuan lebih lanjut.";
            statusText = "DITOLAK";
            statusColor = "#C62828";
        }

        String now = dateTimeUtil.formatToStandardString(LocalDateTime.now());
        String formattedFee = currencyFormatter.formatRupiah(paymentFee);
        String formattedAmount = currencyFormatter.formatRupiah(amount);

        Context context = new Context();
        context.setVariable("intro", intro);
        context.setVariable("customerName", customerName);
        context.setVariable("paymentCode", paymentCode);
        context.setVariable("customerCode", customerCode);
        context.setVariable("paymentFee", formattedFee);
        context.setVariable("paymentType", paymentType);
        context.setVariable("productType", productType);
        context.setVariable("amount", formattedAmount);
        context.setVariable("statusText", statusText);
        context.setVariable("statusColor", statusColor);
        context.setVariable("timestamp", now);

        return templateEngine.process("email-payment", context);
    }

    public String buildForgotPasswordHtml(String customerName, String temporaryPassword) {
        String now = dateTimeUtil.formatToStandardString(LocalDateTime.now());


        Context context = new Context();
        context.setVariable("customerName", customerName);
        context.setVariable("temporaryPassword", temporaryPassword);
        context.setVariable("timestamp", now);

        return templateEngine.process("email-forgot-password", context);
    }
}
