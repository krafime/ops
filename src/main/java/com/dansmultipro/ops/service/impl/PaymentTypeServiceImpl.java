package com.dansmultipro.ops.service.impl;

import com.dansmultipro.ops.dto.paymenttype.PaymentTypeResDTO;
import com.dansmultipro.ops.model.PaymentType;
import com.dansmultipro.ops.repo.PaymentTypeRepo;
import com.dansmultipro.ops.service.PaymentTypeService;
import com.dansmultipro.ops.util.UUIDUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentTypeServiceImpl extends BaseService implements PaymentTypeService {

    private final PaymentTypeRepo paymentTypeRepo;

    public PaymentTypeServiceImpl(PaymentTypeRepo paymentTypeRepo) {
        this.paymentTypeRepo = paymentTypeRepo;
    }

    @Override
    public List<PaymentTypeResDTO> getAllPaymentTypes(Boolean isActive) {
        var paymentTypes = isActive == null ?
                paymentTypeRepo.findAll() :
                paymentTypeRepo.findAllByIsActive(isActive);

        return paymentTypes.stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public PaymentTypeResDTO getPaymentTypeById(String id) {
        var paymentId = UUIDUtil.toUUID(id);
        var paymentType = paymentTypeRepo.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment Type not found"));

        return mapToDTO(paymentType);
    }

    @Override
    public PaymentTypeResDTO getPaymentTypeByCode(String paymentCode) {
        var paymentType = paymentTypeRepo.findByPaymentCode(paymentCode)
                .orElseThrow(() -> new IllegalArgumentException("Payment Type not found"));

        return mapToDTO(paymentType);
    }

    private PaymentTypeResDTO mapToDTO(PaymentType paymentType) {
        return new PaymentTypeResDTO(
                paymentType.getId(),
                paymentType.getPaymentTypeName(),
                paymentType.getPaymentTypeCode(),
                paymentType.getPaymentFee(),
                paymentType.getActive()
        );
    }
}
