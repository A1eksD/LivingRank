package com.livingrank.repository;

import com.livingrank.entity.User;
import com.livingrank.entity.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    Page<User> findByStatus(UserStatus status, Pageable pageable);
    Page<User> findAllByOrderByCreatedAtDesc(Pageable pageable);
    long countByStatus(UserStatus status);

    @Query("SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.displayName) LIKE LOWER(CONCAT('%', :search, '%')) ORDER BY u.createdAt DESC")
    Page<User> searchByEmailOrDisplayName(@Param("search") String search, Pageable pageable);
}
