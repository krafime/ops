package com.dansmultipro.ops.controller;

import com.dansmultipro.ops.dto.paymenttype.PaymentTypeResDTO;
import com.dansmultipro.ops.service.PaymentTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-types")
@Tag(name = "Payment Type Management", description = "API untuk mengelola jenis pembayaran")
public class PaymentTypeController {
    private final PaymentTypeService paymentTypeService;

    public PaymentTypeController(PaymentTypeService paymentTypeService) {
        this.paymentTypeService = paymentTypeService;
    }

    @GetMapping
    @Operation(summary = "Get All Payment Types", description = "Mengambil daftar semua jenis pembayaran, dengan opsi filter berdasarkan status aktif")
    public ResponseEntity<List<PaymentTypeResDTO>> getAllPaymentTypes(@RequestParam(required = false)Boolean isActive) {
        List<PaymentTypeResDTO> paymentTypes = paymentTypeService.getAllPaymentTypes(isActive);
        return new ResponseEntity<>(paymentTypes, HttpStatus.OK);
    }

    @GetMapping("{id}")
    @Operation(summary = "Get Payment Type By ID", description = "Mengambil detail jenis pembayaran berdasarkan ID")
    public ResponseEntity<PaymentTypeResDTO> getPaymentTypeById(@PathVariable String id) {
        PaymentTypeResDTO paymentType = paymentTypeService.getPaymentTypeById(id);
        return new ResponseEntity<>(paymentType, HttpStatus.OK);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get Payment Type By Code", description = "Mengambil detail jenis pembayaran berdasarkan kode")
    public ResponseEntity<PaymentTypeResDTO> getPaymentTypeByCode(@PathVariable String code) {
        PaymentTypeResDTO paymentType = paymentTypeService.getPaymentTypeByCode(code);
        return new ResponseEntity<>(paymentType, HttpStatus.OK);
    }

}
