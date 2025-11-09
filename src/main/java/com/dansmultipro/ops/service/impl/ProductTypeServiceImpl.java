package com.dansmultipro.ops.service.impl;

import com.dansmultipro.ops.dto.general.CommonResDTO;
import com.dansmultipro.ops.dto.producttype.ProductTypeResDTO;
import com.dansmultipro.ops.model.ProductType;
import com.dansmultipro.ops.repo.ProductTypeRepo;
import com.dansmultipro.ops.service.ProductTypeService;
import com.dansmultipro.ops.util.UUIDUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductTypeServiceImpl extends BaseService implements ProductTypeService {

    private final ProductTypeRepo productTypeRepo;

    public ProductTypeServiceImpl(ProductTypeRepo productTypeRepo) {
        this.productTypeRepo = productTypeRepo;
    }

    @Override
    public List<ProductTypeResDTO> getAllProductTypes(Boolean isActive) {
        var productTypes = isActive == null
                ? productTypeRepo.findAll()
                : productTypeRepo.findAllByIsActive(isActive);
        return productTypes.stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public ProductTypeResDTO getProductTypeById(String id) {
        var productId = UUIDUtil.toUUID(id);
        var productType = productTypeRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product type not found"));

        return mapToDTO(productType);
    }

    @Override
    public ProductTypeResDTO getProductTypeByCode(String productCode) {
        var productType = productTypeRepo.findByProductCode(productCode)
                .orElseThrow(() -> new IllegalArgumentException("Product type not found"));

        return mapToDTO(productType);
    }

    @Override
    public CommonResDTO deleteProductType(String id, String productCode) {
        ProductType productType;
        if ((productCode == null && id == null) || (productCode != null && id != null)) {
            throw new IllegalArgumentException("Either id or product code must be provided, but not both");
        } else if (productCode != null) {
            productType = productTypeRepo.findByProductCode(productCode)
                    .orElseThrow(() -> new IllegalArgumentException("Product type not found"));
        } else {
            var productId = UUIDUtil.toUUID(id);
            productType = productTypeRepo.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product type not found"));
        }

        productTypeRepo.save(super.delete(productType));
        return new CommonResDTO(
                "Product type deleted successfully"
        );
    }

    @Override
    public CommonResDTO reactivateProductType(String id, String productCode) {
        ProductType productType;
        if ((productCode == null && id == null) || (productCode != null && id != null)) {
            throw new IllegalArgumentException("Either id or product code must be provided, but not both");
        } else if (productCode != null) {
            productType = productTypeRepo.findByProductCode(productCode)
                    .orElseThrow(() -> new IllegalArgumentException("Product type not found"));
        } else {
            var productId = UUIDUtil.toUUID(id);
            productType = productTypeRepo.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product type not found"));
        }

        productTypeRepo.save(super.reactivate(productType));
        return new CommonResDTO(
                "Product type reactivated successfully"
        );
    }


    private ProductTypeResDTO mapToDTO(ProductType productType) {
        return new ProductTypeResDTO(
                productType.getId(),
                productType.getProductCode(),
                productType.getProductName(),
                productType.getIsPrepaid(),
                productType.getActive()
        );
    }
}
