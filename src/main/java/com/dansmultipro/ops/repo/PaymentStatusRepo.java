package com.dansmultipro.ops.repo;

import com.dansmultipro.ops.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentStatusRepo extends JpaRepository<PaymentStatus, UUID> {
    List<PaymentStatus> findAllByIsActive(Boolean isActive);

    Optional<PaymentStatus> findByStatusCode(String statusCode);
}
