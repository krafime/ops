package com.dansmultipro.ops.controller;

import com.dansmultipro.ops.dto.paymentstatus.PaymentStatusResDTO;
import com.dansmultipro.ops.service.PaymentStatusService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-statuses")
public class PaymentStatusController {
    private final PaymentStatusService paymentStatusService;

    public PaymentStatusController(PaymentStatusService paymentStatusService) {
        this.paymentStatusService = paymentStatusService;
    }

    @GetMapping
    @Operation(summary = "Get all payment statuses", description = "Mengambil semua payment status dengan optional filter isActive")
    public ResponseEntity<List<PaymentStatusResDTO>> getPaymentStatuses(@RequestParam(required = false) Boolean isActive) {
        List<PaymentStatusResDTO> paymentStatuses = paymentStatusService.getAllPaymentStatuses(isActive);
        return new ResponseEntity<>(paymentStatuses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment status by ID", description = "Mengambil payment status berdasarkan ID")
    public ResponseEntity<PaymentStatusResDTO> getPaymentStatusById(@PathVariable String id) {
        PaymentStatusResDTO paymentStatus = paymentStatusService.getPaymentStatusById(id);
        return new ResponseEntity<>(paymentStatus, HttpStatus.OK);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get payment status by code", description = "Mengambil payment status berdasarkan status code (PROCESSING, SUCCESS, FAILED, CANCELLED)")
    public ResponseEntity<PaymentStatusResDTO> getPaymentStatusByCode(@PathVariable String code) {
        PaymentStatusResDTO paymentStatus = paymentStatusService.getPaymentStatusByCode(code);
        return new ResponseEntity<>(paymentStatus, HttpStatus.OK);
    }
}
