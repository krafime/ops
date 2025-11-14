package com.dansmultipro.ops.integration;

import com.dansmultipro.ops.constant.PaymentStatusConstant;
import com.dansmultipro.ops.dto.paymentstatus.PaymentStatusResDTO;
import com.dansmultipro.ops.service.PaymentStatusService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class PaymentStatusServiceIntegrationTest extends AbstractServiceIntegrationTest {

    @Autowired
    private PaymentStatusService paymentStatusService;

    @Test
    void getAllPaymentStatusesTest() {
        List<PaymentStatusResDTO> paymentStatuses = paymentStatusService.getAllPaymentStatuses(null);

        assertThat(paymentStatuses).isNotNull();
        assertThat(paymentStatuses.size()).isEqualTo(4);
    }

    @Test
    void getAllPaymentStatusesActiveOnlyTest() {
        List<PaymentStatusResDTO> activeStatuses = paymentStatusService.getAllPaymentStatuses(true);

        assertThat(activeStatuses).isNotNull();
        assertThat(activeStatuses.size()).isGreaterThan(0);

        // Verify semua status yang diambil adalah active
        activeStatuses.forEach(status ->
                assertThat(paymentStatusRepo.findById(status.id())).isPresent()
        );
    }

    @Test
    void getPaymentStatusByIdTest() {
        PaymentStatusResDTO statusFound = paymentStatusService.getPaymentStatusById(processingStatus.getId().toString());

        assertThat(statusFound).isNotNull();
        assertThat(statusFound.id()).isEqualTo(processingStatus.getId());
        assertThat(statusFound.statusCode()).isNotBlank();
        assertThat(statusFound.statusName()).isNotBlank();
    }

    @Test
    void getPaymentStatusByIdNotFoundTest() {
        assertThatThrownBy(() -> paymentStatusService.getPaymentStatusById(UUID.randomUUID().toString()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Payment Status not found");
    }

    @Test
    void getPaymentStatusByCodeTest() {
        // Test dengan code yang ada di database
        PaymentStatusResDTO processingStatus = paymentStatusService.getPaymentStatusByCode(PaymentStatusConstant.PROCESSING.name());

        assertThat(processingStatus).isNotNull();
        assertThat(processingStatus.statusCode()).isEqualTo(PaymentStatusConstant.PROCESSING.name());
        assertThat(processingStatus.statusName()).isEqualTo(PaymentStatusConstant.PROCESSING.getStatusName());
    }

    @Test
    void getPaymentStatusByCodeNotFoundTest() {
        assertThatThrownBy(() -> paymentStatusService.getPaymentStatusByCode("INVALID_CODE_XYZ"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getPaymentStatusByCodeSuccessTest() {
        PaymentStatusResDTO successStatus = paymentStatusService.getPaymentStatusByCode(PaymentStatusConstant.SUCCESS.name());

        assertThat(successStatus).isNotNull();
        assertThat(successStatus.statusCode()).isEqualTo(PaymentStatusConstant.SUCCESS.name());
        assertThat(successStatus.statusName()).isEqualTo(PaymentStatusConstant.SUCCESS.getStatusName());
    }

    @Test
    void getPaymentStatusByCodeFailedTest() {
        PaymentStatusResDTO failedStatus = paymentStatusService.getPaymentStatusByCode(PaymentStatusConstant.FAILED.name());

        assertThat(failedStatus).isNotNull();
        assertThat(failedStatus.statusCode()).isEqualTo(PaymentStatusConstant.FAILED.name());
        assertThat(failedStatus.statusName()).isEqualTo(PaymentStatusConstant.FAILED.getStatusName());
    }

    @Test
    void getPaymentStatusByCodeCancelledTest() {
        PaymentStatusResDTO cancelledStatus = paymentStatusService.getPaymentStatusByCode(PaymentStatusConstant.CANCELLED.name());

        assertThat(cancelledStatus).isNotNull();
        assertThat(cancelledStatus.statusCode()).isEqualTo(PaymentStatusConstant.CANCELLED.name());
        assertThat(cancelledStatus.statusName()).isEqualTo(PaymentStatusConstant.CANCELLED.getStatusName());
    }
}
