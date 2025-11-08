package com.dansmultipro.ops.repo;

import com.dansmultipro.ops.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentStatusRepo extends JpaRepository<PaymentStatus, UUID> {
    Optional<PaymentStatus> findByStatusCode(String statusCode);
}
