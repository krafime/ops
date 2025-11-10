package com.dansmultipro.ops.controller;

import com.dansmultipro.ops.dto.general.CommonResDTO;
import com.dansmultipro.ops.dto.general.InsertResDTO;
import com.dansmultipro.ops.dto.payment.PaymentCreateReqDTO;
import com.dansmultipro.ops.dto.payment.PaymentPageDTO;
import com.dansmultipro.ops.dto.payment.PaymentResDTO;
import com.dansmultipro.ops.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment Management", description = "API untuk mengelola proses pembayaran")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CUST')")
    @Operation(summary = "Create payment", description = "Customer membuat payment baru dengan status PROCESSING")
    public ResponseEntity<InsertResDTO> createPayment(@Valid @RequestBody PaymentCreateReqDTO paymentReq) {
        var response = paymentService.createPayment(paymentReq);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status/{newStatus}")
    @PreAuthorize("hasAuthority('GTW')")
    @Operation(summary = "Update payment status", description = "User Gateway mengubah status payment dari PROCESSING ke SUCCESS atau FAILED")
    public ResponseEntity<CommonResDTO> updatePaymentStatus(
            @PathVariable String id,
            @PathVariable String newStatus) {
        var response = paymentService.updatePaymentStatus(id, newStatus);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('CUST')")
    @Operation(summary = "Cancel payment", description = "Customer membatalkan payment dengan status PROCESSING")
    public ResponseEntity<CommonResDTO> cancelPayment(@PathVariable String id) {
        var response = paymentService.cancelPayment(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get payment history", description = "Customer melihat history payment mereka, Super Admin melihat semua. Optional filter by status")
    public ResponseEntity<PaymentPageDTO<PaymentResDTO>> getPaymentHistory(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String status) {
        var response = paymentService.getPaymentHistory(page, limit, status);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

