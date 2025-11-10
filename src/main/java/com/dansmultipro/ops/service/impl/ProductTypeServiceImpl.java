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

    private ProductTypeResDTO mapToDTO(ProductType productType) {
        return new ProductTypeResDTO(
                productType.getId(),
                productType.getProductCode(),
                productType.getProductName(),
                productType.getActive()
        );
    }
}
