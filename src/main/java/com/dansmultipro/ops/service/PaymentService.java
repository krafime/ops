package com.dansmultipro.ops.service;

import com.dansmultipro.ops.dto.general.CommonResDTO;
import com.dansmultipro.ops.dto.general.InsertResDTO;
import com.dansmultipro.ops.dto.payment.PaymentCreateReqDTO;
import com.dansmultipro.ops.dto.payment.PaymentPageDTO;
import com.dansmultipro.ops.dto.payment.PaymentResDTO;

public interface PaymentService {
    InsertResDTO createPayment(PaymentCreateReqDTO paymentReq);

    CommonResDTO updatePaymentStatus(String paymentId, String newStatus);

    CommonResDTO cancelPayment(String paymentId);

    PaymentPageDTO<PaymentResDTO> getPaymentHistory(Integer page, Integer limit, String status, String userId);

    PaymentPageDTO<PaymentResDTO> getPaymentHistory(Integer page, Integer limit, String status);
}

