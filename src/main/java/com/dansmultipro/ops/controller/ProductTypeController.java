package com.dansmultipro.ops.controller;

import com.dansmultipro.ops.dto.producttype.ProductTypeResDTO;
import com.dansmultipro.ops.service.ProductTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/product-types")
@Tag(name = "Product Type Management", description = "API untuk mengelola tipe produk (BPJS, PLN, PULSA DLL)")
public class ProductTypeController {
    private final ProductTypeService productTypeService;

    public ProductTypeController(ProductTypeService productTypeService) {
        this.productTypeService = productTypeService;
    }

    @GetMapping
    @Operation(summary = "Get all product types", description = "Mengambil semua product type dengan optional filter isActive")
    public ResponseEntity<List<ProductTypeResDTO>> getAllProductTypes(@RequestParam(required = false) Boolean isActive) {
        List<ProductTypeResDTO> productTypes = productTypeService.getAllProductTypes(isActive);
        return new ResponseEntity<>(productTypes, HttpStatus.OK);
    }

    @GetMapping("{id}")
    @Operation(summary = "Get product type by ID", description = "Mengambil product type berdasarkan ID")
    public ResponseEntity<ProductTypeResDTO> getProductTypeById(@RequestParam String id) {
        ProductTypeResDTO productType = productTypeService.getProductTypeById(id);
        return new ResponseEntity<>(productType, HttpStatus.OK);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get product type by code", description = "Mengambil product type berdasarkan kode produk (e.g., EBOOK, PRINTED, COMBO)")
    public ResponseEntity<ProductTypeResDTO> getProductTypeByCode(@RequestParam String code) {
        ProductTypeResDTO productType = productTypeService.getProductTypeByCode(code);
        return new ResponseEntity<>(productType, HttpStatus.OK);
    }
}
