package com.dansmultipro.ops.service;

import com.dansmultipro.ops.dto.paymenttype.PaymentTypeResDTO;

import java.util.List;

public interface PaymentTypeService {
    List<PaymentTypeResDTO> getAllPaymentTypes(Boolean isActive);

    PaymentTypeResDTO getPaymentTypeById(String id);

    PaymentTypeResDTO getPaymentTypeByCode(String paymentCode);

}
