package com.livingrank.repository;

import com.livingrank.entity.AdminMail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdminMailRepository extends JpaRepository<AdminMail, Long> {

    Page<AdminMail> findAllByOrderBySentAtDesc(Pageable pageable);

    Page<AdminMail> findByRecipientIdOrderBySentAtDesc(UUID recipientId, Pageable pageable);

    Page<AdminMail> findByAdminIdOrderBySentAtDesc(UUID adminId, Pageable pageable);
}
