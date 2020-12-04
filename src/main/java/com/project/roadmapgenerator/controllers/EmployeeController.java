package com.project.roadmapgenerator.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.roadmapgenerator.payloads.EmployeeDto;
import com.project.roadmapgenerator.payloads.EmployeeListWrapperDto;
import com.project.roadmapgenerator.services.EmployeeService;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@PostMapping
	public EmployeeListWrapperDto saveEmployee(@RequestBody EmployeeDto employeePayload) {
		return employeeService.persistEmployeeDetails(employeePayload);
	}

	@PostMapping(path = "/uploadLeaves", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void uploadLeavePlan(@RequestParam("employeeId") Long employeeId,
			@RequestPart("employeeLeaveFile") MultipartFile leaveApplicationFile) throws IOException {
		employeeService.persistEmployeeLeaves(employeeId, leaveApplicationFile);
	}

	@GetMapping
	public EmployeeListWrapperDto getAllEmployees() {
		return employeeService.fetchAllEmployees();
	}

}
