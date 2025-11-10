package com.dansmultipro.ops.repo;

import com.dansmultipro.ops.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
    List<User> findAllByIsActive(Boolean isActive);

    Optional<User> findByEmail(String email);

    Optional<User> findByIdAndIsActive(UUID id, Boolean isActive);

    Optional<User> findByEmailAndIsActive(String email, Boolean isActive);


    List<User> findAllByIsActiveAndRoleTypeRoleCode(Boolean isActive, String roleCode);

    List<User> findAllByRoleTypeRoleCode(String roleCode);

    Optional<User> findByRoleTypeRoleCode(String roleCode);

    Optional<User> findByEmailAndIsActiveTrue(String email);

    Optional<User> findByIdAndIsActiveTrue(UUID id);
}
