package com.project.roadmapgenerator.controllers;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.roadmapgenerator.payloads.ProjectDto;
import com.project.roadmapgenerator.services.ProjectService;


@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {
	
	@Autowired
	private ProjectService projectService;
	
	@PostMapping
	public void saveProject(@RequestBody ProjectDto projectDto, HttpServletResponse response) {
		projectService.persistProjectDetails(projectDto);
	}

}
