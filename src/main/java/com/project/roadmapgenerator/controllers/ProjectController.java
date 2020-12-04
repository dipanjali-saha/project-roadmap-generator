package com.project.roadmapgenerator.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.roadmapgenerator.payloads.ProjectDetailsWrapperDto;
import com.project.roadmapgenerator.services.ProjectService;


@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {
	
	@Autowired
	private ProjectService projectService;
	
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ProjectDetailsWrapperDto saveProject(@RequestPart("projectDetailsFile") MultipartFile projectDetailsFile) throws IOException {
		return projectService.persistProjectDetails(projectDetailsFile);
	}
	
	@GetMapping
	public ProjectDetailsWrapperDto getAllProjects() {
		return projectService.fetchAllProjects();
	}

}
