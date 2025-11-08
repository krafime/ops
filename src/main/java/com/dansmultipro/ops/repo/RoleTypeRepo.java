package com.dansmultipro.ops.repo;

import com.dansmultipro.ops.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleTypeRepo extends JpaRepository<RoleType, UUID> {
    Optional<RoleType> findByRoleCode(String roleCode);

    List<RoleType> findAllByIsActive(Boolean isActive);
}
