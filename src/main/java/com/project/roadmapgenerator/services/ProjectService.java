package com.project.roadmapgenerator.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.roadmapgenerator.entities.Project;
import com.project.roadmapgenerator.repositories.ProjectRepository;

@Service
public class ProjectService {
	
	@Autowired
	private ProjectRepository projectRepository;

	public List<Project> getProjectsByName(String projectName) {
		return projectRepository.findByName(projectName);
	}
	
	public List<Project> getAllProjects() {
		return projectRepository.findAll();
	}
	

}
