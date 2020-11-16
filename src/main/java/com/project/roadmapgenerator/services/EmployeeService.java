package com.project.roadmapgenerator.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.roadmapgenerator.entities.EmployeeEntity;
import com.project.roadmapgenerator.payloads.EmployeeDto;
import com.project.roadmapgenerator.repositories.EmployeeRepository;
import com.project.roadmapgenerator.repositories.ProjectRepository;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private ProjectRepository projectRepository;

	public void persistEmployeeDetails(EmployeeDto employeeDto) {
		if (null != employeeDto) {
			if (null != employeeDto.getEmployeeId()) {
				updateExistingEmployee(employeeDto);
			} else {
				saveNewEmployeeDetails(employeeDto);
			}
		}
	}

	private void saveNewEmployeeDetails(EmployeeDto employeeDto) {
		EmployeeEntity employeeEntity;
		employeeEntity = EmployeeEntity.builder().name(employeeDto.getName())
				.projectId(employeeDto.getAssignedProjectId()).build();
		employeeRepository.save(employeeEntity);
	}

	private void updateExistingEmployee(EmployeeDto employeeDto) {
		EmployeeEntity employeeEntity;
		employeeEntity = employeeRepository.findById(employeeDto.getEmployeeId()).orElse(null);
		if (null != employeeEntity) {
			employeeEntity.setName(employeeDto.getName());
			if (projectRepository.findById(employeeDto.getAssignedProjectId()).isPresent()) {
				employeeEntity.setProjectId(employeeDto.getAssignedProjectId());
			}
			employeeRepository.save(employeeEntity);
		}
	}

}
