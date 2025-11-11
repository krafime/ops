package com.dansmultipro.ops.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class EmailMessageBuilder {

    // --- Public HTML builder (unified) ---------------------------------------

    private final CurrencyFormatter currencyFormatter;

    public EmailMessageBuilder(CurrencyFormatter currencyFormatter) {
        this.currencyFormatter = currencyFormatter;
    }

    public String buildPaymentHtml(
            String customerName,
            String paymentCode,
            String customerCode,
            BigDecimal paymentFee,
            String paymentType,
            String productType,
            BigDecimal amount,
            String status  // "SUCCESS" atau "FAILED"
    ) {
        String intro, statusText, statusColor;

        if ("SUCCESS".equalsIgnoreCase(status)) {
            intro = "Selamat! Pembayaran Anda telah <b>berhasil</b> diproses.";
            statusText = "BERHASIL";
            statusColor = "#0A7C2F";  // green
        } else {
            intro = "Pembayaran Anda telah <b>ditolak</b>.";
            statusText = "DITOLAK";
            statusColor = "#C62828";  // red
        }

        return baseHtml(
                intro,
                customerName,
                paymentCode,
                customerCode,
                paymentFee,
                paymentType,
                productType,
                amount,
                statusText,
                statusColor
        );
    }

    // --- Shared template -----------------------------------------------------

    private String baseHtml(
            String intro,
            String customerName,
            String paymentCode,
            String customerCode,
            BigDecimal paymentFee,
            String paymentType,
            String productType,
            BigDecimal amount,
            String statusText,
            String statusColor
    ) {
        String formattedFee = currencyFormatter.formatRupiah(paymentFee);
        String formattedAmount = currencyFormatter.formatRupiah(amount);
        String now = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm").format(LocalDateTime.now());

        // Inline CSS biar aman di Gmail/Outlook
        return """
                <html>
                  <body style="margin:0;padding:0;background:#f6f7f9;">
                    <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#f6f7f9;">
                      <tr>
                        <td align="center" style="padding:24px;">
                          <table role="presentation" width="600" cellpadding="0" cellspacing="0" 
                                 style="background:#ffffff;border-radius:12px;overflow:hidden;font-family:Arial,Helvetica,sans-serif;color:#111;">
                            <tr>
                              <td style="padding:24px 24px 8px 24px;">
                                <div style="font-size:16px;">Halo %s,</div>
                                <div style="height:8px;"></div>
                                <div style="font-size:14px;line-height:1.6;">%s</div>
                              </td>
                            </tr>
                
                            <tr>
                              <td style="padding:8px 24px 0 24px;">
                                <div style="font-size:15px;font-weight:700;margin-bottom:8px;">Detail Pembayaran</div>
                                <table width="100%%" cellpadding="0" cellspacing="0" 
                                       style="border-collapse:collapse;font-size:14px;">
                                  %s
                                </table>
                              </td>
                            </tr>
                
                            <tr>
                              <td style="padding:16px 24px 0 24px;">
                                <div style="font-size:12px;color:#666;">Dikirim: %s WIB</div>
                              </td>
                            </tr>
                
                            <tr>
                              <td style="padding:24px;">
                                <div style="font-size:14px;line-height:1.6;color:#333;">
                                  Jika ada pertanyaan, silakan hubungi customer service kami.
                                </div>
                                <div style="height:12px;"></div>
                                <div style="font-size:14px;">Salam,<br><b>Tim Online Payment System</b></div>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </body>
                </html>
                """.formatted(
                escape(customerName),
                intro,
                tableRows(
                        "Kode Pembayaran", escape(paymentCode),
                        "Kode Pelanggan", escape(customerCode),
                        "Biaya Administrasi", "Rp. " + formattedFee,
                        "Tipe Pembayaran", escape(paymentType),
                        "Produk", escape(productType),
                        "Jumlah", "Rp. " + formattedAmount,
                        "Status", "<span style=\"font-weight:700;color:" + statusColor + ";\">" + statusText + "</span>"
                ),
                now
        );
    }


    // --- Small helpers -------------------------------------------------------

    // Bikin baris tabel 2 kolom (label & value) yang konsisten
    private String tableRows(Object... kvPairs) {
        StringBuilder rows = new StringBuilder();
        for (int i = 0; i < kvPairs.length; i += 2) {
            String label = String.valueOf(kvPairs[i]);
            String value = String.valueOf(kvPairs[i + 1]);
            rows.append("""
                    <tr>
                      <td style="padding:8px 12px 8px 0;width:210px;vertical-align:top;">
                        <b>%s</b>
                      </td>
                      <td style="padding:8px 0;vertical-align:top;">
                        : %s
                      </td>
                    </tr>
                    """.formatted(label, value));
        }
        return rows.toString();
    }

    // Minimal HTML escape buat konten dinamis
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
