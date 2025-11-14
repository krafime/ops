package com.dansmultipro.ops.integration;

import com.dansmultipro.ops.constant.ProductTypeConstant;
import com.dansmultipro.ops.dto.producttype.ProductTypeResDTO;
import com.dansmultipro.ops.service.ProductTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class ProductTypeServiceIntegrationTest extends AbstractServiceIntegrationTest {

    @Autowired
    private ProductTypeService productTypeService;

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
        ProductTypeResDTO productTypeFound = productTypeService.getProductTypeById(defaultProductType.getId().toString());

        assertThat(productTypeFound).isNotNull();
        assertThat(productTypeFound.id()).isEqualTo(defaultProductType.getId());
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
    void getProductTypeByIdInvalidUUIDTest() {
        assertThatThrownBy(() -> productTypeService.getProductTypeById("invalid-uuid-string"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid UUID format: invalid-uuid-string");
    }

    @Test
    void getProductTypeByIdNullTest() {
        assertThatThrownBy(() -> productTypeService.getProductTypeById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("UUID string cannot be null or empty");
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
    void getProductTypeByCodeNullTest() {
        assertThatThrownBy(() -> productTypeService.getProductTypeByCode(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getProductTypeByCodeEmptyTest() {
        assertThatThrownBy(() -> productTypeService.getProductTypeByCode(""))
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
