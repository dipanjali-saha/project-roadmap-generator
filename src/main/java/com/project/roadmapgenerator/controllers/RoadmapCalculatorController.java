package com.project.roadmapgenerator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.roadmapgenerator.services.RoadmapCalculatorService;

@RestController
@RequestMapping("/api/v1/project-roadmap")
public class RoadmapCalculatorController {
	
	@Autowired
	private RoadmapCalculatorService roadmapCalculatorService;
	
	@PostMapping("/project/{projectName}")
	public void generateProjectRoadmap(@PathVariable("projectName") String projectName) {
		roadmapCalculatorService.calculateProjectEndDate(projectName);
	}
	
	

}
