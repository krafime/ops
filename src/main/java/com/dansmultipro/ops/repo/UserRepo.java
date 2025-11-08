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

    List<User> findAllByRoleTypeAndIsActive(String roleType, Boolean isActive);

    Optional<User> findByIdAndActiveTrue(UUID id);
}
