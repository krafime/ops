package com.dansmultipro.ops.service;

import com.dansmultipro.ops.dto.producttype.ProductTypeResDTO;

import java.util.List;

public interface ProductTypeService {
    List<ProductTypeResDTO> getAllProductTypes(Boolean isActive);

    ProductTypeResDTO getProductTypeById(String id);

    ProductTypeResDTO getProductTypeByCode(String productCode);
}
