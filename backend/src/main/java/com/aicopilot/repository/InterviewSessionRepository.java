package com.aicopilot.repository;

import com.aicopilot.model.InterviewSession;
import com.aicopilot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewSessionRepository extends JpaRepository<InterviewSession, Long> {
    List<InterviewSession> findByUserOrderByCreatedAtDesc(User user);
    List<InterviewSession> findByUserIdOrderByCreatedAtDesc(Long userId);
}
