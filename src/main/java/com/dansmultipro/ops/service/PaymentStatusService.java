package com.dansmultipro.ops.service;

import com.dansmultipro.ops.dto.paymentstatus.PaymentStatusResDTO;

import java.util.List;

public interface PaymentStatusService {
    List<PaymentStatusResDTO> getAllPaymentStatuses(Boolean isActive);

    PaymentStatusResDTO getPaymentStatusById(String id);

    PaymentStatusResDTO getPaymentStatusByCode(String code);
}
