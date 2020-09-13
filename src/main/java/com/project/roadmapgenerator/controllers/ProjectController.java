package com.project.roadmapgenerator.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.roadmapgenerator.entities.Project;
import com.project.roadmapgenerator.services.ProjectService;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

	@Autowired
	private ProjectService projectService;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Project> fetchAllProjects() {		
		return projectService.getAllProjects();
	}

}