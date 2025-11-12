package com.dansmultipro.ops.integration;

import com.dansmultipro.ops.constant.ProductTypeConstant;
import com.dansmultipro.ops.dto.producttype.ProductTypeResDTO;
import com.dansmultipro.ops.model.ProductType;
import com.dansmultipro.ops.repo.ProductTypeRepo;
import com.dansmultipro.ops.service.ProductTypeService;
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
public class ProductTypeServiceIntegrationTest {

    private static final UUID SYSTEM_ADMIN_UUID = UUID.fromString("792b3990-9315-405a-ba5a-da07770c1edf");
    @Autowired
    private ProductTypeRepo productTypeRepo;
    @Autowired
    private ProductTypeService productTypeService;

    @BeforeEach
    void setUp() {
        // Clear existing data
        productTypeRepo.deleteAll();

        // Create product types sesuai dengan database-script.sql menggunakan ProductTypeConstant
        // 1. PLN
        ProductType productTypePLN = new ProductType();
        productTypePLN.setId(UUID.randomUUID());
        productTypePLN.setProductCode(ProductTypeConstant.PLN.name());
        productTypePLN.setProductName(ProductTypeConstant.PLN.getProductName());
        productTypePLN.setActive(true);
        productTypePLN.setCreatedAt(LocalDateTime.now());
        productTypePLN.setCreatedBy(SYSTEM_ADMIN_UUID);
        productTypeRepo.save(productTypePLN);

        // 2. PULSA
        ProductType productTypePULSA = new ProductType();
        productTypePULSA.setId(UUID.randomUUID());
        productTypePULSA.setProductCode(ProductTypeConstant.PULSA.name());
        productTypePULSA.setProductName(ProductTypeConstant.PULSA.getProductName());
        productTypePULSA.setActive(true);
        productTypePULSA.setCreatedAt(LocalDateTime.now());
        productTypePULSA.setCreatedBy(SYSTEM_ADMIN_UUID);
        productTypeRepo.save(productTypePULSA);

        // 3. INTERNET
        ProductType productTypeINTERNET = new ProductType();
        productTypeINTERNET.setId(UUID.randomUUID());
        productTypeINTERNET.setProductCode(ProductTypeConstant.INTERNET.name());
        productTypeINTERNET.setProductName(ProductTypeConstant.INTERNET.getProductName());
        productTypeINTERNET.setActive(true);
        productTypeINTERNET.setCreatedAt(LocalDateTime.now());
        productTypeINTERNET.setCreatedBy(SYSTEM_ADMIN_UUID);
        productTypeRepo.save(productTypeINTERNET);

        // 4. BPJS
        ProductType productTypeBPJS = new ProductType();
        productTypeBPJS.setId(UUID.randomUUID());
        productTypeBPJS.setProductCode(ProductTypeConstant.BPJS.name());
        productTypeBPJS.setProductName(ProductTypeConstant.BPJS.getProductName());
        productTypeBPJS.setActive(true);
        productTypeBPJS.setCreatedAt(LocalDateTime.now());
        productTypeBPJS.setCreatedBy(SYSTEM_ADMIN_UUID);
        productTypeRepo.save(productTypeBPJS);

        // 5. PDAM
        ProductType productTypePDAM = new ProductType();
        productTypePDAM.setId(UUID.randomUUID());
        productTypePDAM.setProductCode(ProductTypeConstant.PDAM.name());
        productTypePDAM.setProductName(ProductTypeConstant.PDAM.getProductName());
        productTypePDAM.setActive(true);
        productTypePDAM.setCreatedAt(LocalDateTime.now());
        productTypePDAM.setCreatedBy(SYSTEM_ADMIN_UUID);
        productTypeRepo.save(productTypePDAM);
    }

    @Test
    void getAllProductTypesTest() {
        List<ProductTypeResDTO> productTypes = productTypeService.getAllProductTypes(null);

        assertThat(productTypes).isNotNull();
        assertThat(productTypes.size()).isEqualTo(5);
    }

    @Test
    void getAllProductTypesActiveOnlyTest() {
        List<ProductTypeResDTO> activeProductTypes = productTypeService.getAllProductTypes(true);

        assertThat(activeProductTypes).isNotNull();
        assertThat(activeProductTypes.size()).isGreaterThan(0);

        // Verify semua product type yang diambil adalah active
        activeProductTypes.forEach(productType ->
                assertThat(productTypeRepo.findById(productType.id())).isPresent()
        );
    }

    @Test
    void getProductTypeByIdTest() {
        // Ambil semua product types, ambil yang pertama untuk test
        List<ProductTypeResDTO> productTypes = productTypeService.getAllProductTypes(null);

        assertThat(productTypes).isNotNull();
        assertThat(productTypes.size()).isGreaterThan(0);

        UUID productTypeId = productTypes.get(0).id();
        ProductTypeResDTO productTypeFound = productTypeService.getProductTypeById(productTypeId.toString());

        assertThat(productTypeFound).isNotNull();
        assertThat(productTypeFound.id()).isEqualTo(productTypeId);
        assertThat(productTypeFound.productCode()).isNotBlank();
        assertThat(productTypeFound.productName()).isNotBlank();
    }

    @Test
    void getProductTypeByIdNotFoundTest() {
        assertThatThrownBy(() -> productTypeService.getProductTypeById(UUID.randomUUID().toString()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product type not found");
    }

    @Test
    void getProductTypeByCodeTest() {
        // Test dengan code yang ada di database
        ProductTypeResDTO plnProductType = productTypeService.getProductTypeByCode(ProductTypeConstant.PLN.name());

        assertThat(plnProductType).isNotNull();
        assertThat(plnProductType.productCode()).isEqualTo(ProductTypeConstant.PLN.name());
        assertThat(plnProductType.productName()).isEqualTo(ProductTypeConstant.PLN.getProductName());
    }

    @Test
    void getProductTypeByCodeNotFoundTest() {
        assertThatThrownBy(() -> productTypeService.getProductTypeByCode("INVALID_CODE_XYZ"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getProductTypeByCodePULSATest() {
        ProductTypeResDTO pulsaProductType = productTypeService.getProductTypeByCode(ProductTypeConstant.PULSA.name());

        assertThat(pulsaProductType).isNotNull();
        assertThat(pulsaProductType.productCode()).isEqualTo(ProductTypeConstant.PULSA.name());
        assertThat(pulsaProductType.productName()).isEqualTo(ProductTypeConstant.PULSA.getProductName());
    }

    @Test
    void getProductTypeByCodeINTERNETTest() {
        ProductTypeResDTO internetProductType = productTypeService.getProductTypeByCode(ProductTypeConstant.INTERNET.name());

        assertThat(internetProductType).isNotNull();
        assertThat(internetProductType.productCode()).isEqualTo(ProductTypeConstant.INTERNET.name());
        assertThat(internetProductType.productName()).isEqualTo(ProductTypeConstant.INTERNET.getProductName());
    }

    @Test
    void getProductTypeByCodeBPJSTest() {
        ProductTypeResDTO bpjsProductType = productTypeService.getProductTypeByCode(ProductTypeConstant.BPJS.name());

        assertThat(bpjsProductType).isNotNull();
        assertThat(bpjsProductType.productCode()).isEqualTo(ProductTypeConstant.BPJS.name());
        assertThat(bpjsProductType.productName()).isEqualTo(ProductTypeConstant.BPJS.getProductName());
    }

    @Test
    void getProductTypeByCodePDAMTest() {
        ProductTypeResDTO pdamProductType = productTypeService.getProductTypeByCode(ProductTypeConstant.PDAM.name());

        assertThat(pdamProductType).isNotNull();
        assertThat(pdamProductType.productCode()).isEqualTo(ProductTypeConstant.PDAM.name());
        assertThat(pdamProductType.productName()).isEqualTo(ProductTypeConstant.PDAM.getProductName());
    }
}

