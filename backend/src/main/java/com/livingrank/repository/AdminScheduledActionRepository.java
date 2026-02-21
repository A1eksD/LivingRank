package com.livingrank.repository;

import com.livingrank.entity.AdminScheduledAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AdminScheduledActionRepository extends JpaRepository<AdminScheduledAction, Long> {

    List<AdminScheduledAction> findByExecutedFalseAndCancelledFalseAndDeadlineBefore(LocalDateTime deadline);

    Page<AdminScheduledAction> findByExecutedFalseAndCancelledFalseOrderByDeadlineAsc(Pageable pageable);

    Page<AdminScheduledAction> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<AdminScheduledAction> findByTargetUserIdOrderByCreatedAtDesc(UUID targetUserId, Pageable pageable);

    long countByExecutedFalseAndCancelledFalse();
}
