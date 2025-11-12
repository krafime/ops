package com.dansmultipro.ops.integration;

import com.dansmultipro.ops.constant.PaymentStatusConstant;
import com.dansmultipro.ops.dto.paymentstatus.PaymentStatusResDTO;
import com.dansmultipro.ops.model.PaymentStatus;
import com.dansmultipro.ops.repo.PaymentStatusRepo;
import com.dansmultipro.ops.service.PaymentStatusService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
public class PaymentStatusServiceIntegrationTest {

    private static final UUID SYSTEM_ADMIN_UUID = UUID.fromString("792b3990-9315-405a-ba5a-da07770c1edf");
    @Autowired
    private PaymentStatusRepo paymentStatusRepo;
    @Autowired
    private PaymentStatusService paymentStatusService;

    @BeforeEach
    void setUp() {
        // Clear existing data
        paymentStatusRepo.deleteAll();

        // Create payment statuses sesuai dengan database-script.sql menggunakan PaymentStatusConstant
        // 1. PROCESSING
        PaymentStatus statusProcessing = new PaymentStatus();
        statusProcessing.setId(UUID.randomUUID());
        statusProcessing.setStatusCode(PaymentStatusConstant.PROCESSING.name());
        statusProcessing.setStatusName(PaymentStatusConstant.PROCESSING.getStatusName());
        statusProcessing.setActive(true);
        statusProcessing.setCreatedAt(LocalDateTime.now());
        statusProcessing.setCreatedBy(SYSTEM_ADMIN_UUID);
        paymentStatusRepo.save(statusProcessing);

        // 2. SUCCESS
        PaymentStatus statusSuccess = new PaymentStatus();
        statusSuccess.setId(UUID.randomUUID());
        statusSuccess.setStatusCode(PaymentStatusConstant.SUCCESS.name());
        statusSuccess.setStatusName(PaymentStatusConstant.SUCCESS.getStatusName());
        statusSuccess.setActive(true);
        statusSuccess.setCreatedAt(LocalDateTime.now());
        statusSuccess.setCreatedBy(SYSTEM_ADMIN_UUID);
        paymentStatusRepo.save(statusSuccess);

        // 3. FAILED
        PaymentStatus statusFailed = new PaymentStatus();
        statusFailed.setId(UUID.randomUUID());
        statusFailed.setStatusCode(PaymentStatusConstant.FAILED.name());
        statusFailed.setStatusName(PaymentStatusConstant.FAILED.getStatusName());
        statusFailed.setActive(true);
        statusFailed.setCreatedAt(LocalDateTime.now());
        statusFailed.setCreatedBy(SYSTEM_ADMIN_UUID);
        paymentStatusRepo.save(statusFailed);

        // 4. CANCELLED
        PaymentStatus statusCancelled = new PaymentStatus();
        statusCancelled.setId(UUID.randomUUID());
        statusCancelled.setStatusCode(PaymentStatusConstant.CANCELLED.name());
        statusCancelled.setStatusName(PaymentStatusConstant.CANCELLED.getStatusName());
        statusCancelled.setActive(true);
        statusCancelled.setCreatedAt(LocalDateTime.now());
        statusCancelled.setCreatedBy(SYSTEM_ADMIN_UUID);
        paymentStatusRepo.save(statusCancelled);
    }

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
        // Ambil semua payment statuses, ambil yang pertama untuk test
        List<PaymentStatusResDTO> paymentStatuses = paymentStatusService.getAllPaymentStatuses(null);

        assertThat(paymentStatuses).isNotNull();
        assertThat(paymentStatuses.size()).isGreaterThan(0);

        UUID statusId = paymentStatuses.get(0).id();
        PaymentStatusResDTO statusFound = paymentStatusService.getPaymentStatusById(statusId.toString());

        assertThat(statusFound).isNotNull();
        assertThat(statusFound.id()).isEqualTo(statusId);
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
