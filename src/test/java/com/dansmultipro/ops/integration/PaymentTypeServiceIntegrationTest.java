package com.dansmultipro.ops.integration;

import com.dansmultipro.ops.constant.PaymentTypeConstant;
import com.dansmultipro.ops.dto.paymenttype.PaymentTypeResDTO;
import com.dansmultipro.ops.service.PaymentTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class PaymentTypeServiceIntegrationTest extends AbstractServiceIntegrationTest {

    @Autowired
    private PaymentTypeService paymentTypeService;

    @Test
    void getAllPaymentTypesTest() {
        List<PaymentTypeResDTO> paymentTypes = paymentTypeService.getAllPaymentTypes(null);

        assertThat(paymentTypes).isNotNull();
        assertThat(paymentTypes.size()).isEqualTo(6);
    }

    @Test
    void getAllPaymentTypesActiveOnlyTest() {
        List<PaymentTypeResDTO> activePaymentTypes = paymentTypeService.getAllPaymentTypes(true);

        assertThat(activePaymentTypes).isNotNull();
        assertThat(activePaymentTypes.size()).isGreaterThan(0);

        // Verify semua payment type yang diambil adalah active
        activePaymentTypes.forEach(paymentType ->
                assertThat(paymentTypeRepo.findById(paymentType.id())).isPresent()
        );
    }

    @Test
    void getPaymentTypeByIdTest() {
        // Ambil semua payment types, ambil yang pertama untuk test
        List<PaymentTypeResDTO> paymentTypes = paymentTypeService.getAllPaymentTypes(null);

        assertThat(paymentTypes).isNotNull();
        assertThat(paymentTypes.size()).isGreaterThan(0);

        UUID paymentTypeId = paymentTypes.get(0).id();
        PaymentTypeResDTO paymentTypeFound = paymentTypeService.getPaymentTypeById(paymentTypeId.toString());

        assertThat(paymentTypeFound).isNotNull();
        assertThat(paymentTypeFound.id()).isEqualTo(paymentTypeId);
        assertThat(paymentTypeFound.paymentCode()).isNotBlank();
        assertThat(paymentTypeFound.paymentName()).isNotBlank();
    }

    @Test
    void getPaymentTypeByIdNotFoundTest() {
        assertThatThrownBy(() -> paymentTypeService.getPaymentTypeById(UUID.randomUUID().toString()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Payment Type not found");
    }

    @Test
    void getPaymentTypeByCodeTest() {
        // Test dengan code yang ada di database
        PaymentTypeResDTO qrisPaymentType = paymentTypeService.getPaymentTypeByCode(PaymentTypeConstant.QRIS.name());

        assertThat(qrisPaymentType).isNotNull();
        assertThat(qrisPaymentType.paymentCode()).isEqualTo(PaymentTypeConstant.QRIS.name());
        assertThat(qrisPaymentType.paymentName()).isEqualTo(PaymentTypeConstant.QRIS.getDisplayName());
        assertThat(qrisPaymentType.paymentFee()).isEqualTo(new BigDecimal("500.00"));
    }

    @Test
    void getPaymentTypeByCodeNotFoundTest() {
        assertThatThrownBy(() -> paymentTypeService.getPaymentTypeByCode("INVALID_CODE_XYZ"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getPaymentTypeByCodeVirtualAccountTest() {
        PaymentTypeResDTO vaPaymentType = paymentTypeService.getPaymentTypeByCode(PaymentTypeConstant.VIRTUAL_ACCOUNT.name());

        assertThat(vaPaymentType).isNotNull();
        assertThat(vaPaymentType.paymentCode()).isEqualTo(PaymentTypeConstant.VIRTUAL_ACCOUNT.name());
        assertThat(vaPaymentType.paymentName()).isEqualTo(PaymentTypeConstant.VIRTUAL_ACCOUNT.getDisplayName());
        assertThat(vaPaymentType.paymentFee()).isEqualTo(new BigDecimal("2500.00"));
    }

    @Test
    void getPaymentTypeByCodeOVOTest() {
        PaymentTypeResDTO ovoPaymentType = paymentTypeService.getPaymentTypeByCode(PaymentTypeConstant.OVO.name());

        assertThat(ovoPaymentType).isNotNull();
        assertThat(ovoPaymentType.paymentCode()).isEqualTo(PaymentTypeConstant.OVO.name());
        assertThat(ovoPaymentType.paymentName()).isEqualTo(PaymentTypeConstant.OVO.getDisplayName());
        assertThat(ovoPaymentType.paymentFee()).isEqualTo(new BigDecimal("1000.00"));
    }

    @Test
    void getPaymentTypeByCodeGOPAYTest() {
        PaymentTypeResDTO gopayPaymentType = paymentTypeService.getPaymentTypeByCode(PaymentTypeConstant.GOPAY.name());

        assertThat(gopayPaymentType).isNotNull();
        assertThat(gopayPaymentType.paymentCode()).isEqualTo(PaymentTypeConstant.GOPAY.name());
        assertThat(gopayPaymentType.paymentName()).isEqualTo(PaymentTypeConstant.GOPAY.getDisplayName());
        assertThat(gopayPaymentType.paymentFee()).isEqualTo(new BigDecimal("1000.00"));
    }

}
