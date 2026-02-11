package com.portfolio.repository;

import com.portfolio.model.Role;
import com.portfolio.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByFirebaseUid(String firebaseUid);

    boolean existsByEmail(String email);

    Page<User> findByRoleAndIsActiveTrue(Role role, Pageable pageable);

    Page<User> findByIsActiveTrue(Pageable pageable);

    List<User> findByRoleAndIsActiveTrue(Role role);

    List<User> findByIsActiveTrue();
}
