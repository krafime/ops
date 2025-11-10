package com.dansmultipro.ops.repo;

import com.dansmultipro.ops.model.Payment;
import com.dansmultipro.ops.model.PaymentStatus;
import com.dansmultipro.ops.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByPaymentCode(String paymentCode);

    Page<Payment> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    Page<Payment> findByUserAndPaymentStatusOrderByCreatedAtDesc(User user, PaymentStatus paymentStatus, Pageable pageable);

    Page<Payment> findByPaymentStatusOrderByCreatedAtDesc(PaymentStatus paymentStatus, Pageable pageable);

    Page<Payment> findAllByOrderByCreatedAtDesc(Pageable pageable);
}

