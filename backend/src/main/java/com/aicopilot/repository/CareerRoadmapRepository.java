package com.aicopilot.repository;

import com.aicopilot.model.CareerRoadmap;
import com.aicopilot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerRoadmapRepository extends JpaRepository<CareerRoadmap, Long> {
    List<CareerRoadmap> findByUserOrderByCreatedAtDesc(User user);
    List<CareerRoadmap> findByUserIdOrderByCreatedAtDesc(Long userId);
}
