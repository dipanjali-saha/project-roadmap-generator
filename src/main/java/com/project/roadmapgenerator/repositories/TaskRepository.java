package com.project.roadmapgenerator.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.roadmapgenerator.entities.TaskEntity;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
	Optional<TaskEntity> findById(Long taskId);
	Optional<TaskEntity> findByPriorityAndMilestoneId(int priority, Long milestoneId);
}
