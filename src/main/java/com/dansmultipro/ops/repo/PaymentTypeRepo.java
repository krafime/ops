package com.dansmultipro.ops.repo;

import com.dansmultipro.ops.model.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentTypeRepo extends JpaRepository<PaymentType, UUID> {
    Optional<PaymentType> findByPaymentCode(String paymentCode);
}
