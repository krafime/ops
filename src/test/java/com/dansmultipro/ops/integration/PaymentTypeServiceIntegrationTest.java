package com.dansmultipro.ops.integration;

import com.dansmultipro.ops.constant.PaymentTypeConstant;
import com.dansmultipro.ops.dto.paymenttype.PaymentTypeResDTO;
import com.dansmultipro.ops.model.PaymentType;
import com.dansmultipro.ops.repo.PaymentTypeRepo;
import com.dansmultipro.ops.service.PaymentTypeService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
public class PaymentTypeServiceIntegrationTest {

    private static final UUID SYSTEM_ADMIN_UUID = UUID.fromString("792b3990-9315-405a-ba5a-da07770c1edf");
    @Autowired
    private PaymentTypeRepo paymentTypeRepo;
    @Autowired
    private PaymentTypeService paymentTypeService;

    @BeforeEach
    void setUp() {
        // Clear existing data
        paymentTypeRepo.deleteAll();

        // Create payment types sesuai dengan database-script.sql menggunakan PaymentTypeConstant
        // 1. QRIS
        PaymentType paymentTypeQRIS = new PaymentType();
        paymentTypeQRIS.setId(UUID.randomUUID());
        paymentTypeQRIS.setPaymentTypeCode(PaymentTypeConstant.QRIS.name());
        paymentTypeQRIS.setPaymentTypeName(PaymentTypeConstant.QRIS.getDisplayName());
        paymentTypeQRIS.setPaymentFee(new BigDecimal("500.00"));
        paymentTypeQRIS.setActive(true);
        paymentTypeQRIS.setCreatedAt(LocalDateTime.now());
        paymentTypeQRIS.setCreatedBy(SYSTEM_ADMIN_UUID);
        paymentTypeRepo.save(paymentTypeQRIS);

        // 2. VIRTUAL_ACCOUNT
        PaymentType paymentTypeVA = new PaymentType();
        paymentTypeVA.setId(UUID.randomUUID());
        paymentTypeVA.setPaymentTypeCode(PaymentTypeConstant.VIRTUAL_ACCOUNT.name());
        paymentTypeVA.setPaymentTypeName(PaymentTypeConstant.VIRTUAL_ACCOUNT.getDisplayName());
        paymentTypeVA.setPaymentFee(new BigDecimal("2500.00"));
        paymentTypeVA.setActive(true);
        paymentTypeVA.setCreatedAt(LocalDateTime.now());
        paymentTypeVA.setCreatedBy(SYSTEM_ADMIN_UUID);
        paymentTypeRepo.save(paymentTypeVA);

        // 3. SHOPPE_PAY
        PaymentType paymentTypeShoppe = new PaymentType();
        paymentTypeShoppe.setId(UUID.randomUUID());
        paymentTypeShoppe.setPaymentTypeCode(PaymentTypeConstant.SHOPPE_PAY.name());
        paymentTypeShoppe.setPaymentTypeName(PaymentTypeConstant.SHOPPE_PAY.getDisplayName());
        paymentTypeShoppe.setPaymentFee(new BigDecimal("1000.00"));
        paymentTypeShoppe.setActive(true);
        paymentTypeShoppe.setCreatedAt(LocalDateTime.now());
        paymentTypeShoppe.setCreatedBy(SYSTEM_ADMIN_UUID);
        paymentTypeRepo.save(paymentTypeShoppe);

        // 4. OVO
        PaymentType paymentTypeOVO = new PaymentType();
        paymentTypeOVO.setId(UUID.randomUUID());
        paymentTypeOVO.setPaymentTypeCode(PaymentTypeConstant.OVO.name());
        paymentTypeOVO.setPaymentTypeName(PaymentTypeConstant.OVO.getDisplayName());
        paymentTypeOVO.setPaymentFee(new BigDecimal("1000.00"));
        paymentTypeOVO.setActive(true);
        paymentTypeOVO.setCreatedAt(LocalDateTime.now());
        paymentTypeOVO.setCreatedBy(SYSTEM_ADMIN_UUID);
        paymentTypeRepo.save(paymentTypeOVO);

        // 5. DANA
        PaymentType paymentTypeDANA = new PaymentType();
        paymentTypeDANA.setId(UUID.randomUUID());
        paymentTypeDANA.setPaymentTypeCode(PaymentTypeConstant.DANA.name());
        paymentTypeDANA.setPaymentTypeName(PaymentTypeConstant.DANA.getDisplayName());
        paymentTypeDANA.setPaymentFee(new BigDecimal("1000.00"));
        paymentTypeDANA.setActive(true);
        paymentTypeDANA.setCreatedAt(LocalDateTime.now());
        paymentTypeDANA.setCreatedBy(SYSTEM_ADMIN_UUID);
        paymentTypeRepo.save(paymentTypeDANA);

        // 6. GOPAY
        PaymentType paymentTypeGOPAY = new PaymentType();
        paymentTypeGOPAY.setId(UUID.randomUUID());
        paymentTypeGOPAY.setPaymentTypeCode(PaymentTypeConstant.GOPAY.name());
        paymentTypeGOPAY.setPaymentTypeName(PaymentTypeConstant.GOPAY.getDisplayName());
        paymentTypeGOPAY.setPaymentFee(new BigDecimal("1000.00"));
        paymentTypeGOPAY.setActive(true);
        paymentTypeGOPAY.setCreatedAt(LocalDateTime.now());
        paymentTypeGOPAY.setCreatedBy(SYSTEM_ADMIN_UUID);
        paymentTypeRepo.save(paymentTypeGOPAY);
    }

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

