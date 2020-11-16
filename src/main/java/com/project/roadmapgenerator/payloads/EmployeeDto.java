package com.project.roadmapgenerator.payloads;

import lombok.Data;

@Data
public class EmployeeDto {
	
	private Long employeeId;
	private String name;
	private Long assignedProjectId;

}
