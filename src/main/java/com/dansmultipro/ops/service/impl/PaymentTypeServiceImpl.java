package com.dansmultipro.ops.service.impl;

import com.dansmultipro.ops.dto.general.CommonResDTO;
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
    public List<PaymentTypeResDTO> getPaymentTypes(Boolean isActive) {
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

    @Override
    public CommonResDTO deletePaymentType(String id, String paymentCode) {
        PaymentType paymentType;
        if ((paymentCode == null && id == null) || (paymentCode != null && id != null)) {
            throw new IllegalArgumentException("Either id or paymentCode must be provided, but not both");
        } else if (paymentCode != null) {
            paymentType = paymentTypeRepo.findByPaymentCode(paymentCode)
                    .orElseThrow(() -> new IllegalArgumentException("Payment Type not found"));
        } else {
            var paymentId = UUIDUtil.toUUID(id);
            paymentType = paymentTypeRepo.findById(paymentId)
                    .orElseThrow(() -> new IllegalArgumentException("Payment Type not found"));
        }

        paymentTypeRepo.save(super.delete(paymentType));
        return new CommonResDTO("Payment Type deleted successfully");
    }

    @Override
    public CommonResDTO reactivatePaymentType(String id, String paymentCode) {
        PaymentType paymentType;
        if ((paymentCode == null && id == null) || (paymentCode != null && id != null)) {
            throw new IllegalArgumentException("Either id or paymentCode must be provided, but not both");
        } else if (paymentCode != null) {
            paymentType = paymentTypeRepo.findByPaymentCode(paymentCode)
                    .orElseThrow(() -> new IllegalArgumentException("Payment Type not found"));
        } else {
            var paymentId = UUIDUtil.toUUID(id);
            paymentType = paymentTypeRepo.findById(paymentId)
                    .orElseThrow(() -> new IllegalArgumentException("Payment Type not found"));
        }

        paymentTypeRepo.save(super.reactivate(paymentType));
        return new CommonResDTO("Payment Type reactivated successfully");
    }

    private PaymentTypeResDTO mapToDTO(PaymentType paymentType) {
        return new PaymentTypeResDTO(
                paymentType.getId(),
                paymentType.getPaymentTypeCode(),
                paymentType.getPaymentTypeName(),
                paymentType.getPaymentFee(),
                paymentType.getActive()
        );
    }
}
