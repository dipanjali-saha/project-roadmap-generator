package com.project.roadmapgenerator.payloads;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDto {
	
	private Long employeeId;
	private String name;
	private Long assignedProjectId;
	private String assignedProjectName;

}
