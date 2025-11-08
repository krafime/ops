package com.dansmultipro.ops.repo;

import com.dansmultipro.ops.model.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductTypeRepo extends JpaRepository<ProductType, UUID> {
    Optional<ProductType> findByProductCode(String productCode);
}
