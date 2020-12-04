package com.project.roadmapgenerator.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.roadmapgenerator.entities.EmployeeLeaveEntity;

@Repository
public interface EmployeeLeaveRepository extends JpaRepository<EmployeeLeaveEntity, Long> {
	List<EmployeeLeaveEntity> findByEmployeeId(Long employeeId);
}
