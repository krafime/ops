package com.dansmultipro.ops.service;

import com.dansmultipro.ops.dto.general.CommonResDTO;
import com.dansmultipro.ops.dto.paymenttype.PaymentTypeResDTO;

import java.util.List;

public interface PaymentTypeService {
    List<PaymentTypeResDTO> getPaymentTypes(Boolean isActive);

    PaymentTypeResDTO getPaymentTypeById(String id);

    PaymentTypeResDTO getPaymentTypeByCode(String paymentCode);

    CommonResDTO deletePaymentType(String id, String paymentCode);

    CommonResDTO reactivatePaymentType(String id, String paymentCode);

}
