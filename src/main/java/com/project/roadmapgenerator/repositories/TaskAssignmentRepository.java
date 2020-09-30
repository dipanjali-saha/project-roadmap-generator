package com.project.roadmapgenerator.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.roadmapgenerator.entities.TaskAssignmentEntity;

@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignmentEntity, Long>{

}
