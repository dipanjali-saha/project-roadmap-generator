package com.project.roadmapgenerator.payloads;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class EmployeeLeaveApplicationDto {
	
	private Long employeeId;
	private MultipartFile leaveExcel;

}
