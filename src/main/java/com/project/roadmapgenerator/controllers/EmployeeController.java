package com.project.roadmapgenerator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.roadmapgenerator.payloads.EmployeeDto;
import com.project.roadmapgenerator.services.EmployeeService;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {
	
	@Autowired
	private EmployeeService employeeService;
	
	@PostMapping
	public void saveEmployee(@RequestBody EmployeeDto employeePayload) {
		employeeService.persistEmployeeDetails(employeePayload);
	}

}
