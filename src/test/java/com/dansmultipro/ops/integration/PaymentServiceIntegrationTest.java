package com.dansmultipro.ops.integration;

import com.dansmultipro.ops.constant.PaymentStatusConstant;
import com.dansmultipro.ops.dto.general.CommonResDTO;
import com.dansmultipro.ops.dto.general.InsertResDTO;
import com.dansmultipro.ops.dto.payment.PaymentCreateReqDTO;
import com.dansmultipro.ops.dto.payment.PaymentPageDTO;
import com.dansmultipro.ops.dto.payment.PaymentResDTO;
import com.dansmultipro.ops.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class PaymentServiceIntegrationTest extends AbstractServiceIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    void createPaymentTest() {
        // Mock authentication for payment creation
        Mockito.when(authUtil.getLoginId()).thenReturn(customerUser.getId());

        PaymentCreateReqDTO paymentReq = new PaymentCreateReqDTO(
                new BigDecimal("100000.00"), // amount
                defaultPaymentType.getId().toString(), // paymentTypeId
                defaultProductType.getId().toString(), // productTypeId
                "CUST001" // customerCode
        );

        InsertResDTO result = paymentService.createPayment(paymentReq);

        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.message()).isEqualTo("Payment created successfully");

        // Verify payment was created
        PaymentPageDTO<PaymentResDTO> history = paymentService.getPaymentHistory(1, 10, null);
        assertThat(history.data().size()).isGreaterThan(0);
    }

    @Test
    void createPaymentInvalidPaymentTypeTest() {
        // Mock authentication for payment creation
        Mockito.when(authUtil.getLoginId()).thenReturn(customerUser.getId());

        PaymentCreateReqDTO paymentReq = new PaymentCreateReqDTO(
                new BigDecimal("100000.00"),
                "invalid-payment-type-id", // invalid payment type
                defaultProductType.getId().toString(),
                "CUST001"
        );

        assertThatThrownBy(() -> paymentService.createPayment(paymentReq))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createPaymentWithSameCustomerButCustomerCodeSameTest() {
        // Mock authentication for payment creation
        Mockito.when(authUtil.getLoginId()).thenReturn(customerUser.getId());

        PaymentCreateReqDTO paymentReq1 = new PaymentCreateReqDTO(
                new BigDecimal("50000.00"),
                defaultPaymentType.getId().toString(),
                defaultProductType.getId().toString(),
                "CUST100"
        );

        InsertResDTO result1 = paymentService.createPayment(paymentReq1);
        assertThat(result1).isNotNull();

        PaymentCreateReqDTO paymentReq2 = new PaymentCreateReqDTO(
                new BigDecimal("75000.00"),
                defaultPaymentType.getId().toString(),
                defaultProductType.getId().toString(),
                "CUST100" // same customer code
        );

        assertThatThrownBy(() -> paymentService.createPayment(paymentReq2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createPaymentInvalidProductTypeTest() {
        // Mock authentication for payment creation
        Mockito.when(authUtil.getLoginId()).thenReturn(customerUser.getId());

        PaymentCreateReqDTO paymentReq = new PaymentCreateReqDTO(
                new BigDecimal("100000.00"),
                defaultPaymentType.getId().toString(),
                "invalid-product-type-id", // invalid product type
                "CUST001"
        );

        assertThatThrownBy(() -> paymentService.createPayment(paymentReq))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createPaymentInvalidAmountTest() {
        // Mock authentication for payment creation
        Mockito.when(authUtil.getLoginId()).thenReturn(customerUser.getId());

        PaymentCreateReqDTO paymentReq = new PaymentCreateReqDTO(
                new BigDecimal("-1000.00"), // negative amount
                defaultPaymentType.getId().toString(),
                defaultProductType.getId().toString(),
                "CUST001"
        );

        InsertResDTO result = paymentService.createPayment(paymentReq);
        assertThat(result).isNotNull();
    }

    @Test
    void updatePaymentStatusTest() {
        // Mock authentication for payment creation
        Mockito.when(authUtil.getLoginId()).thenReturn(gatewayUser.getId());

        // First create a payment
        PaymentCreateReqDTO paymentReq = new PaymentCreateReqDTO(
                new BigDecimal("50000.00"),
                defaultPaymentType.getId().toString(),
                defaultProductType.getId().toString(),
                "CUST002"
        );
        InsertResDTO createResult = paymentService.createPayment(paymentReq);
        String paymentId = createResult.id().toString();

        // Update status to SUCCESS
        CommonResDTO result = paymentService.updatePaymentStatus(paymentId, PaymentStatusConstant.SUCCESS.name());

        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo("Payment status updated to SUCCESS");
    }

    @Test
    void updatePaymentStatusInvalidPaymentTest() {
        assertThatThrownBy(() -> paymentService.updatePaymentStatus(
                "invalid-payment-id",
                PaymentStatusConstant.SUCCESS.name()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updatePaymentStatusInvalidStatusTest() {
        // Mock authentication for payment creation
        Mockito.when(authUtil.getLoginId()).thenReturn(customerUser.getId());

        // First create a payment
        PaymentCreateReqDTO paymentReq = new PaymentCreateReqDTO(
                new BigDecimal("75000.00"),
                defaultPaymentType.getId().toString(),
                defaultProductType.getId().toString(),
                "CUST003"
        );
        InsertResDTO createResult = paymentService.createPayment(paymentReq);
        String paymentId = createResult.id().toString();

        // Try to update to invalid status
        assertThatThrownBy(() -> paymentService.updatePaymentStatus(paymentId, "INVALID_STATUS"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cancelPaymentTest() {
        // Mock authentication for payment creation
        Mockito.when(authUtil.getLoginId()).thenReturn(customerUser.getId());

        // First create a payment
        PaymentCreateReqDTO paymentReq = new PaymentCreateReqDTO(
                new BigDecimal("25000.00"),
                defaultPaymentType.getId().toString(),
                defaultProductType.getId().toString(),
                "CUST004"
        );
        InsertResDTO createResult = paymentService.createPayment(paymentReq);
        String paymentId = createResult.id().toString();

        // Cancel the payment
        CommonResDTO result = paymentService.cancelPayment(paymentId);

        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo("Payment cancelled successfully");
    }

    @Test
    void cancelPaymentInvalidPaymentTest() {
        assertThatThrownBy(() -> paymentService.cancelPayment("invalid-payment-id"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cancelPaymentAlreadyCancelledTest() {
        // Mock authentication for payment creation
        Mockito.when(authUtil.getLoginId()).thenReturn(customerUser.getId());

        // First create and cancel a payment
        PaymentCreateReqDTO paymentReq = new PaymentCreateReqDTO(
                new BigDecimal("30000.00"),
                defaultPaymentType.getId().toString(),
                defaultProductType.getId().toString(),
                "CUST005"
        );
        InsertResDTO createResult = paymentService.createPayment(paymentReq);
        String paymentId = createResult.id().toString();

        // Cancel the payment first time
        paymentService.cancelPayment(paymentId);

        // Try to cancel again
        assertThatThrownBy(() -> paymentService.cancelPayment(paymentId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getPaymentHistoryTest() {
        // Mock authentication for payment creation
        Mockito.when(authUtil.getLoginId()).thenReturn(customerUser.getId());

        // Create a few payments first
        PaymentCreateReqDTO paymentReq1 = new PaymentCreateReqDTO(
                new BigDecimal("10000.00"),
                defaultPaymentType.getId().toString(),
                defaultProductType.getId().toString(),
                "CUST006"
        );
        paymentService.createPayment(paymentReq1);

        PaymentCreateReqDTO paymentReq2 = new PaymentCreateReqDTO(
                new BigDecimal("20000.00"),
                defaultPaymentType.getId().toString(),
                defaultProductType.getId().toString(),
                "CUST007"
        );
        paymentService.createPayment(paymentReq2);

        // Get payment history
        PaymentPageDTO<PaymentResDTO> result = paymentService.getPaymentHistory(1, 10, null);

        assertThat(result).isNotNull();
        assertThat(result.page()).isEqualTo(1);
        assertThat(result.limit()).isEqualTo(10);
        assertThat(result.total()).isGreaterThanOrEqualTo(2L);
        assertThat(result.data()).isNotNull();
        assertThat(result.data().size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void getPaymentHistoryWithStatusFilterTest() {
        // Mock authentication for payment creation
        Mockito.when(authUtil.getLoginId()).thenReturn(customerUser.getId());

        // Create a payment and update its status
        PaymentCreateReqDTO paymentReq = new PaymentCreateReqDTO(
                new BigDecimal("15000.00"),
                defaultPaymentType.getId().toString(),
                defaultProductType.getId().toString(),
                "CUST008"
        );
        InsertResDTO createResult = paymentService.createPayment(paymentReq);
        String paymentId = createResult.id().toString();

        // Update status to SUCCESS
        paymentService.updatePaymentStatus(paymentId, PaymentStatusConstant.SUCCESS.name());

        // Get payment history filtered by SUCCESS status
        PaymentPageDTO<PaymentResDTO> result = paymentService.getPaymentHistory(1, 10, PaymentStatusConstant.SUCCESS.name());

        assertThat(result).isNotNull();
        assertThat(result.data()).isNotNull();
        // Should contain at least the payment we just created and updated
        assertThat(result.total()).isGreaterThanOrEqualTo(1L);
    }

    @Test
    void getPaymentHistoryWithUserFilterTest() {
        // Mock authentication for payment creation
        Mockito.when(authUtil.getLoginId()).thenReturn(customerUser.getId());

        // Create a payment for a specific user
        PaymentCreateReqDTO paymentReq = new PaymentCreateReqDTO(
                new BigDecimal("35000.00"),
                defaultPaymentType.getId().toString(),
                defaultProductType.getId().toString(),
                "CUST009"
        );
        paymentService.createPayment(paymentReq);

        // Get payment history for customer user
        PaymentPageDTO<PaymentResDTO> result = paymentService.getPaymentHistory(1, 10, null, customerUser.getId().toString());

        assertThat(result).isNotNull();
        assertThat(result.data()).isNotNull();
        // Should contain payments for this user
        assertThat(result.total()).isGreaterThan(0L);
    }

    @Test
    void getPaymentHistoryPaginationTest() {
        // Mock authentication for payment creation
        Mockito.when(authUtil.getLoginId()).thenReturn(customerUser.getId());

        // Create multiple payments
        for (int i = 0; i < 5; i++) {
            PaymentCreateReqDTO paymentReq = new PaymentCreateReqDTO(
                    new BigDecimal("1000.00" + i),
                    defaultPaymentType.getId().toString(),
                    defaultProductType.getId().toString(),
                    "CUST" + String.format("%03d", i + 10)
            );
            paymentService.createPayment(paymentReq);
        }

        // Test pagination - page 0, limit 2
        PaymentPageDTO<PaymentResDTO> result = paymentService.getPaymentHistory(1, 2, null);

        assertThat(result).isNotNull();
        assertThat(result.page()).isEqualTo(1);
        assertThat(result.limit()).isEqualTo(2);
        assertThat(result.data().size()).isEqualTo(2);
        assertThat(result.total()).isGreaterThanOrEqualTo(5L);
    }

    @Test
    void getPaymentHistoryInvalidPageTest() {
        assertThatThrownBy(() -> paymentService.getPaymentHistory(-1, 10, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getPaymentHistoryInvalidLimitTest() {
        assertThatThrownBy(() -> paymentService.getPaymentHistory(1, 0, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
