package com.aicopilot.repository;

import com.aicopilot.model.JobMatch;
import com.aicopilot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobMatchRepository extends JpaRepository<JobMatch, Long> {
    List<JobMatch> findByUserOrderByCreatedAtDesc(User user);
    List<JobMatch> findByUserIdOrderByCreatedAtDesc(Long userId);
}
