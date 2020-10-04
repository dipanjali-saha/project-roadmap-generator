package com.project.roadmapgenerator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.roadmapgenerator.payloads.ProjectDto;
import com.project.roadmapgenerator.services.ProjectRoadmapDetailsService;
import com.project.roadmapgenerator.services.RoadmapCalculatorService;

@RestController
@RequestMapping("/api/v1/project-roadmap")
public class ProjectRoadmapController {
	
	@Autowired
	private RoadmapCalculatorService roadmapCalculatorService;
	@Autowired
	private ProjectRoadmapDetailsService projectRoadmapDetailsService;
	
	@PostMapping("/project/{projectName}")
	public void generateProjectRoadmap(@PathVariable("projectName") String projectName) {
		roadmapCalculatorService.calculateProjectRoadmap(projectName);
	}
	
	@GetMapping("/project/{projectName}")
	public ProjectDto getProjectRoadmap(@PathVariable("projectName") String projectName) {
		return projectRoadmapDetailsService.fetchRoadmapForProject(projectName);
	}

}
