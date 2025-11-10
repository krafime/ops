package com.dansmultipro.ops.service.impl;

import com.dansmultipro.ops.dto.paymentstatus.PaymentStatusResDTO;
import com.dansmultipro.ops.model.PaymentStatus;
import com.dansmultipro.ops.repo.PaymentStatusRepo;
import com.dansmultipro.ops.service.PaymentStatusService;
import com.dansmultipro.ops.util.UUIDUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentStatusServiceImpl  implements PaymentStatusService {

    private final PaymentStatusRepo paymentStatusRepo;

    public PaymentStatusServiceImpl(PaymentStatusRepo paymentStatusRepo) {
        this.paymentStatusRepo = paymentStatusRepo;
    }

    @Override
    public List<PaymentStatusResDTO> getAllPaymentStatuses(Boolean isActive) {
        var paymentStatuses = isActive == null ?
                paymentStatusRepo.findAll():
                paymentStatusRepo.findAllByIsActive(isActive);

        return paymentStatuses.stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public PaymentStatusResDTO getPaymentStatusById(String id) {
        var paymentId = UUIDUtil.toUUID(id);
        var paymentStatus = paymentStatusRepo.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment Status not found"));
        return  mapToDTO(paymentStatus);
    }

    @Override
    public PaymentStatusResDTO getPaymentStatusByCode(String code) {
        var paymentStatus = paymentStatusRepo.findByStatusCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Payment Status not found"));
        return mapToDTO(paymentStatus);
    }

    private PaymentStatusResDTO mapToDTO(PaymentStatus paymentStatus) {
        return new PaymentStatusResDTO(
                paymentStatus.getId(),
                paymentStatus.getStatusCode(),
                paymentStatus.getStatusName(),
                paymentStatus.getActive()
        );
    }
}

