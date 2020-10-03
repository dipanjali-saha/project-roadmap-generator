package com.project.roadmapgenerator.repositories;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.roadmapgenerator.entities.TaskAssignmentEntity;

@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignmentEntity, Long>{
	
	Optional<TaskAssignmentEntity> findByTaskId(Long taskId);
	List<TaskAssignmentEntity> findByTaskIdIn(List<Long> taskIds);
	List<TaskAssignmentEntity> findByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long employeeId, Date startDateComparedTo, Date endDateComparedTo);

}
